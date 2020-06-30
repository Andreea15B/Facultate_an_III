# INTERFATA

"""
Patrat rosu:      agent
Patrate negre:    obstacole    [recompensa = -1]
Cerc verde:       sfarsit      [recompensa = +1]
Restul tablei:    drum liber   [recompensa = 0]
"""

import numpy as np
import time
import tkinter as tk    # interfata

UNIT = 40   # pixels
MAZE_H = 4  # grid height
MAZE_W = 4  # grid width


class Maze(tk.Tk, object):
    def __init__(self):
        super(Maze, self).__init__()
        self.action_space = ['u', 'd', 'l', 'r']    # up down left right
        self.nr_actions = len(self.action_space)
        self.title('Q-Learning Maze')
        self.geometry('{0}x{1}'.format(MAZE_H * UNIT, MAZE_H * UNIT))
        self._build_maze()

    def _build_maze(self):
        self.canvas = tk.Canvas(self, bg='white', height=MAZE_H * UNIT, width=MAZE_W * UNIT)

        # creeaza tabel
        for column in range(0, MAZE_W * UNIT, UNIT):
            x0, y0, x1, y1 = column, 0, column, MAZE_H * UNIT
            self.canvas.create_line(x0, y0, x1, y1)
        for row in range(0, MAZE_H * UNIT, UNIT):
            x0, y0, x1, y1 = 0, row, MAZE_W * UNIT, row
            self.canvas.create_line(x0, y0, x1, y1)

        # creeaza originea
        origin = np.array([20, 20])

        # creeaza obstacol nr1
        obstacle1_center = origin + np.array([UNIT * 2, UNIT])
        self.obstacle1 = self.canvas.create_rectangle(
            obstacle1_center[0] - 15, obstacle1_center[1] - 15,
            obstacle1_center[0] + 15, obstacle1_center[1] + 15,
            fill='black')
        # creeaza obstacol nr2
        obstacle2_center = origin + np.array([UNIT, UNIT * 2])
        self.obstacle2 = self.canvas.create_rectangle(
            obstacle2_center[0] - 15, obstacle2_center[1] - 15,
            obstacle2_center[0] + 15, obstacle2_center[1] + 15,
            fill='black')

        # create end
        end_center = origin + UNIT * 2
        self.end = self.canvas.create_oval(
            end_center[0] - 15, end_center[1] - 15,
            end_center[0] + 15, end_center[1] + 15,
            fill='green')

        # creeaza agent
        self.agent = self.canvas.create_rectangle(
            origin[0] - 15, origin[1] - 15,
            origin[0] + 15, origin[1] + 15,
            fill='red')

        self.canvas.pack()

    def reset(self):
        self.update()
        time.sleep(0.5)
        self.canvas.delete(self.agent)
        origin = np.array([20, 20])
        self.agent = self.canvas.create_rectangle(
            origin[0] - 15, origin[1] - 15,
            origin[0] + 15, origin[1] + 15,
            fill='red')
        return self.canvas.coords(self.agent)   # observatie

    def step(self, action):
        s = self.canvas.coords(self.agent)
        base_action = np.array([0, 0])
        if action == 0:   # up
            if s[1] > UNIT:
                base_action[1] -= UNIT
        elif action == 1:   # down
            if s[1] < (MAZE_H - 1) * UNIT:
                base_action[1] += UNIT
        elif action == 2:   # right
            if s[0] < (MAZE_W - 1) * UNIT:
                base_action[0] += UNIT
        elif action == 3:   # left
            if s[0] > UNIT:
                base_action[0] -= UNIT

        self.canvas.move(self.agent, base_action[0], base_action[1])  # mutare agent

        state_ = self.canvas.coords(self.agent)  # urmatoarea stare

        # recompense
        if state_ == self.canvas.coords(self.end):
            reward = 1
            done = True
            state_ = 'terminal'
        elif state_ in [self.canvas.coords(self.obstacle1), self.canvas.coords(self.obstacle2)]:
            reward = -1
            done = True
            state_ = 'terminal'
        else:
            reward = 0
            done = False

        return state_, reward, done

    def render(self):
        time.sleep(0.1)
        self.update()


#####################################
# ALGORITMUL RL

import pandas

class QLearningTable:
    def __init__(self, actions, rata_invatare=0.01, scadere_recompensa=0.9, epsilon_greedy=0.9):
        self.actions = actions  # lista
        self.rata_invatare = rata_invatare
        self.gamma = scadere_recompensa
        self.epsilon = epsilon_greedy
        self.q_table = pandas.DataFrame(columns=self.actions, dtype=np.float64)

    def choose_action(self, observation):
        self.check_state_exist(observation)
        # selectie actiune
        if np.random.uniform() < self.epsilon:
            # alege cea mai buna actiune
            state_action = self.q_table.loc[observation, :]
            # alege random intre actiunile cu aceeasi valoare
            action = np.random.choice(state_action[state_action == np.max(state_action)].index)
        else:
            # alege o actiune random
            action = np.random.choice(self.actions)
        return action

    def learn(self, state, a, r, state_):
        self.check_state_exist(state_)
        q_predict = self.q_table.loc[state, a]
        if state_ != 'terminal':
            q_target = r + self.gamma * self.q_table.loc[state_, :].max()  # urmatoarea stare nu e terminala
        else:
            q_target = r  # urmatoarea stare este terminala
        self.q_table.loc[state, a] += self.rata_invatare * (q_target - q_predict)  # update

    def check_state_exist(self, state):
        if state not in self.q_table.index:
            # adauga starea noua la tabel
            self.q_table = self.q_table.append(
                pandas.Series(
                    [0]*len(self.actions),
                    index=self.q_table.columns,
                    name=state,
                )
            )


#####################################
# MAIN

def update():
    for episode in range(10):
        # observatia initiala
        observation = maze.reset()

        while True:
            maze.render()

            # alege actiunea in functie de observatie
            action = RL.choose_action(str(observation))

            # face actiunea si obtine urmatoarea observatie si recompensa
            observation_, reward, done = maze.step(action)

            RL.learn(str(observation), action, reward, str(observation_))

            observation = observation_
            if done:
                break

    print('Game over.')
    maze.destroy()

if __name__ == "__main__":
    maze = Maze()
    actions = list(range(maze.nr_actions))
    RL = QLearningTable(actions)

    maze.after(100, update)
    maze.mainloop()