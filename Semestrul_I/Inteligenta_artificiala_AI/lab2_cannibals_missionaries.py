import random
import time

# cannibals = 4
# missionaries = 4
# boat_capacity = 3
numberIterations = 10
numberMaximTransitions = 100
maxDepth = 999


class State:
    def __init__(self, nr_cannibals_st, nr_missionaries_st, nr_cannibals_dr, nr_missionaries_dr, boat_position, heuristic):
        self.nr_cannibals_st = nr_cannibals_st
        self.nr_missionaries_st = nr_missionaries_st
        self.nr_cannibals_dr = nr_cannibals_dr
        self.nr_missionaries_dr = nr_missionaries_dr
        self.boat_position = boat_position
        self.heuristic = heuristic
        self.parent = None
        self.visited = False

    def isFinal(self):
        return self.nr_cannibals_st == 0 and self.nr_missionaries_st == 0

    def isValid(self):
        if self.boat_position == 1 and (self.nr_cannibals_st + self.nr_missionaries_st <= 0):
            return False
        if self.boat_position == 2 and (self.nr_cannibals_dr + self.nr_missionaries_dr <= 0):
            return False
        return (self.nr_cannibals_st <= self.nr_missionaries_st or self.nr_missionaries_st == 0) \
               and (self.nr_cannibals_dr <= self.nr_missionaries_dr or self.nr_missionaries_dr == 0) \
               and (self.nr_cannibals_st + self.nr_cannibals_dr == cannibals and self.nr_missionaries_dr + self.nr_missionaries_st == missionaries)

    def isValid_forAstar(self):
        if self.nr_missionaries_st >= 0 and self.nr_missionaries_dr >= 0 \
                and self.nr_cannibals_st >= 0 and self.nr_cannibals_dr >= 0 \
                and (self.nr_missionaries_st == 0 or self.nr_missionaries_st >= self.nr_cannibals_st) \
                and (self.nr_missionaries_dr == 0 or self.nr_missionaries_dr >= self.nr_cannibals_dr):
            return True
        else:
            return False

    # turns object into string
    def __str__(self):
        return f'cannibals_st: {self.nr_cannibals_st}, missionaries_st: {self.nr_missionaries_st}, ' \
               f'cannibals_dr: {self.nr_cannibals_dr}, missionaries_dr: {self.nr_missionaries_dr}, boat_position: {self.boat_position}'

    def __eq__(self, other):
        return self.nr_cannibals_st == other.nr_cannibals_st and self.nr_missionaries_st == other.nr_missionaries_st \
               and self.nr_cannibals_dr == other.nr_cannibals_dr and self.nr_missionaries_dr == other.nr_missionaries_dr \
               and self.boat_position == other.boat_position and self.visited == other.visited

    def __hash__(self):
        return hash((self.nr_cannibals_st, self.nr_missionaries_st, self.nr_cannibals_dr, self.nr_missionaries_dr, self.boat_position))

class Transition:
    def __init__(self, cannibals, missionaries):
        self.cannibals = cannibals
        self.missionaries = missionaries

    def __str__(self):
        return f'cannibals: {self.cannibals}, missionaries: {self.missionaries}'

    def __eq__(self, other):
        return self.cannibals == other.cannibals and self.missionaries == other.missionaries


def isValidTransition(transition):
    if transition is None:
        return False
    if transition.cannibals == 0 and transition.missionaries == 0:
        return False
    if transition.cannibals + transition.missionaries > boat_capacity:
        return False
    if transition.cannibals > transition.missionaries and transition.missionaries > 0:
        return False
    if transition.cannibals < 0 or transition.missionaries < 0:
        return False
    return True

def getRandomTransition(State):
    if State.boat_position == 1:
        cannibals = State.nr_cannibals_st
        missionaries = State.nr_missionaries_st
    else:
        cannibals = State.nr_cannibals_dr
        missionaries = State.nr_missionaries_dr

    cannibals_random = random.randint(0, min(cannibals, boat_capacity))
    if cannibals_random > min(missionaries, boat_capacity - cannibals_random):
        return Transition(cannibals_random, 0)
    missionaries_random = random.randint(cannibals_random, min(missionaries, boat_capacity - cannibals_random))
    return Transition(cannibals_random, missionaries_random)

def makeNewState(state, transition):
    if state.boat_position == 1:
        return State(state.nr_cannibals_st - transition.cannibals, state.nr_missionaries_st - transition.missionaries,
                         state.nr_cannibals_dr + transition.cannibals, state.nr_missionaries_dr + transition.missionaries, 2, 0)
    else:
        return State(state.nr_cannibals_st + transition.cannibals, state.nr_missionaries_st + transition.missionaries,
                         state.nr_cannibals_dr - transition.cannibals, state.nr_missionaries_dr - transition.missionaries, 1, 0)

def RandomStrategy():
    print("\n--- Random Strategy ---")
    initialState = State(nr_cannibals_st=cannibals, nr_missionaries_st=missionaries, nr_cannibals_dr=0, nr_missionaries_dr=0, boat_position=1, heuristic=0)
    print("Stare initiala: ", initialState)
    iteratia = 0
    done = False
    for i in range(numberIterations):
        # initial state
        currentState = State(nr_cannibals_st=cannibals, nr_missionaries_st=missionaries, nr_cannibals_dr=0, nr_missionaries_dr=0, boat_position=1, heuristic=0)
        currentState.visited = True
        for _ in range(numberMaximTransitions):
            transition = getRandomTransition(currentState)
            if isValidTransition(transition) == True:
                # update new state
                if makeNewState(currentState, transition).visited == False:
                    currentState = makeNewState(currentState, transition)
                    currentState.visited = True

            elif currentState.isFinal() == True:
                # print("Solutia finala: ", currentState.nr_cannibals_st, currentState.nr_missionaries_st,
                #       currentState.nr_missionaries_dr, currentState.nr_missionaries_dr)
                print("Solutie finala: ", currentState)
                # print("Gasit la iteratia: ", i+1)
                iteratia = i+1
                done = True
                break
        # if currentState.isFinal() == False:
        #     print("Iteratia", i+1, ":", currentState.nr_cannibals_st, currentState.nr_missionaries_st,
        #           currentState.nr_missionaries_dr, currentState.nr_missionaries_dr)
        if done is True:
            break

    if currentState.isFinal() == False:
        print("Nu s-a gasit nicio solutie!\n")
    return iteratia

def makeAllPossibleStates():
    states = []
    for cannibals_st in range(0, cannibals + 1):
        for missionaries_st in range(0, missionaries + 1):
            for cannibals_dr in range(0, cannibals + 1):
                for missionaries_dr in range(0, missionaries + 1):
                    for boat_position in [1, 2]:
                        state = State(cannibals_st, missionaries_st, cannibals_dr, missionaries_dr, boat_position, 0)
                        if state.isValid():
                            states.append(state)
    return states

def makeEdgesBetweenStates(states):
    matrix = [None] * len(states)
    for i, state1 in enumerate(states):
        for j, state2 in enumerate(states):
            transition = makeTransitionBetweenStates(state1, state2)
            if isValidTransition(transition):
                if matrix[i] is not None:
                    matrix[i].append(j)
                else:
                    matrix[i] = [j]
    return matrix

def makeTransitionBetweenStates(state1, state2):
    if state1.boat_position == state2.boat_position:
        return None
    if state1.boat_position == 1:
        return Transition(state2.nr_cannibals_dr - state1.nr_cannibals_dr, state2.nr_missionaries_dr - state1.nr_missionaries_dr)
    return Transition(state2.nr_cannibals_st - state1.nr_cannibals_st, state2.nr_missionaries_st - state1.nr_missionaries_st)

def backtrackingStrategy(state, transitionsDone=[], done=[False]):
    global states
    state.visited = True
    if state.isFinal():
        print(f'Solutie finala: {state}')
        done[0] = True
    if done[0] == True:
        return

    for newState in states:
        if newState.visited == True:
            continue
        transition = makeTransitionBetweenStates(state, newState)
        if isValidTransition(transition):
            transitionsDone.append(newState)
            backtrackingStrategy(newState, transitionsDone, done)
            if (done[0] == True):
                return transitionsDone
            transitionsDone.pop(len(transitionsDone)-1)
            newState.visited = False
            
def DFS(limit, stateIndex, statesTraversed, done=[False]):
    global states, matrix
    states[stateIndex].visited = True
    if states[stateIndex].isFinal():
        print(f'Solutie finala: {states[stateIndex]}')
        done[0] = True
    if limit == 0:
        return
    for i in matrix[stateIndex][::-1]:
        if states[i].visited == False:
            statesTraversed.append(states[i])
            DFS(limit-1, i, statesTraversed, done)
            if done[0] == True:
                return
            statesTraversed.pop(len(statesTraversed)-1)
            states[i].visited = False

def runBktStrategy():
    print("\n--- Backtracking Strategy ---")
    global states
    states = makeAllPossibleStates()
    initialState = State(cannibals, missionaries, 0, 0, 1, 0)
    print(f'Stare initiala: {initialState}')
    transitionsDone = backtrackingStrategy(initialState, [initialState])
    if transitionsDone is None:
        print("Nu s-a gasit nicio solutie!")
        return 0
    else:
        return len(transitionsDone)
    # print steps:
    # if transitionsDone is not None and transitionsDone[-1].isFinal():
    #     for state in transitionsDone:
    #         print(state)

def runIddfsStrategy():
    print("\n--- IDDFS Strategy ---")
    initialState = State(nr_cannibals_st=cannibals, nr_missionaries_st=missionaries, nr_cannibals_dr=0,
                         nr_missionaries_dr=0, boat_position=1, heuristic=0)
    print("Stare initiala: ", initialState)

    global states, matrix
    states = makeAllPossibleStates()
    matrix = makeEdgesBetweenStates(states)
    indexInitialState = ([i for (i, state) in enumerate(states)
                          if state.nr_cannibals_st == cannibals and state.nr_missionaries_st == missionaries and state.boat_position == 1])[0]
    iteratii = 0
    for i in range(0, maxDepth):
        statesTraversed = [states[indexInitialState]]
        DFS(i, indexInitialState, statesTraversed)
        # print steps:
        if statesTraversed[-1].isFinal():
            for state in statesTraversed:
                iteratii += 1
                # print(state)
            flag = 1
            break
        else:
            flag = 0
    if flag == 0:
        print("Nu s-a gasit nicio solutie!")
    return iteratii

def heuristic_function(state):
    global boat_capacity
    return (state.nr_cannibals_st + state.nr_missionaries_st)/boat_capacity

def getChildren(cur_state):
    children = list()
    if cur_state.boat_position == 1:
        for cannibalss in range(0, cur_state.nr_cannibals_st+1):
            for missionariess in range(0, cur_state.nr_missionaries_st+1):
                new_state = State(cur_state.nr_cannibals_st - cannibalss, cur_state.nr_missionaries_st - missionariess,
                                  cur_state.nr_cannibals_dr + cannibalss, cur_state.nr_missionaries_dr + missionariess,
                                  cur_state.boat_position,heuristic_function(cur_state))
                if new_state.isValid_forAstar():
                    new_state.parent = cur_state
                    children.append(new_state)
    else:
        for cannibalss in range(0, cur_state.nr_cannibals_dr+1):
            for missionariess in range(0, cur_state.nr_missionaries_dr+1):
                new_state = State(cur_state.nr_cannibals_st + cannibalss, cur_state.nr_missionaries_st + missionariess,
                                  cur_state.nr_cannibals_dr - cannibalss, cur_state.nr_missionaries_dr - missionariess,
                                  cur_state.boat_position,heuristic_function(cur_state))
                if new_state.isValid_forAstar():
                    new_state.parent = cur_state
                    children.append(new_state)
    return children

def Astar_strategy():
    global boat_capacity
    print("\n--- A Star Strategy ---")
    h = (cannibals + missionaries)/ boat_capacity
    initial_state = State(cannibals, missionaries, 0, 0, 1, h)
    print("Stare initiala: ", initial_state)
    iteratii = 0

    nodes = list()
    visited = set()
    nodes.append(initial_state)

    while nodes:
        costs = list()
        for node in nodes:
            costs.append(node.heuristic)
        index = costs.index(min(costs))
        state = nodes.pop(index)
        iteratii += 1

        if state.isFinal():
            print("Solutie finala: ", state)
            return iteratii

        visited.add(state)
        children = getChildren(state)
        for child in children:
            if (child in visited) and (state.heuristic < child.heuristic):
                child.heuristic = state.heuristic
                child.parent = state
            elif (child in nodes) and (state.heuristic < child.heuristic):
                child.heuristic = state.heuristic
                child.parent = state
            else:
                nodes.append(child)
                child.heuristic = state.heuristic
    print("Nu s-a gasit nicio solutie!")
    return 0

def Tema1():
    strategy = int(input("Selecteaza strategia: 1 - Random; 2 - Backtracking; 3 - IDDFS \n"))
    if strategy == 1:
        RandomStrategy()
    elif strategy == 2:
        runBktStrategy()
    else:
        runIddfsStrategy()

def Tema2():
    global missionaries, cannibals, boat_capacity
    timpMediu_randomStrategy = 0
    timpMediu_bktStrategy = 0
    timpMediu_IddfsStrategy = 0
    timpMediu_AstarStrategy = 0
    iteratii_randomStrategy = 0
    iteratii_bktStrategy = 0
    iteratii_IddfsStrategy = 0
    iteratii_AstarStrategy = 0
    for i in range(1, 10):
        print("\nGeneram instante random: ")
        missionaries = random.randint(3, 15)
        cannibals = random.randint(3, 15)
        while cannibals > missionaries:
            cannibals = random.randint(3, 15)
        boat_capacity = random.randint(2, 5)
        print("Missionaries: ", missionaries)
        print("Cannibals: ", cannibals)
        print("Boat capacity: ", boat_capacity)

        start_time = time.time()
        iteratia = RandomStrategy()
        iteratii_randomStrategy += iteratia
        end_time = time.time()
        timpMediu_randomStrategy += end_time - start_time
        print("\n")

        start_time = time.time()
        iteratia = runBktStrategy()
        iteratii_bktStrategy += iteratia
        end_time = time.time()
        timpMediu_bktStrategy += end_time - start_time
        print("\n")

        start_time = time.time()
        iteratia = runIddfsStrategy()
        iteratii_IddfsStrategy += iteratia
        end_time = time.time()
        timpMediu_IddfsStrategy += end_time - start_time
        print("\n")

        start_time = time.time()
        iteratia = Astar_strategy()
        iteratii_AstarStrategy += iteratia
        end_time = time.time()
        timpMediu_AstarStrategy += end_time - start_time
        print("\n")

    timpMediu_randomStrategy /= 10
    timpMediu_bktStrategy /= 10
    timpMediu_IddfsStrategy /= 10
    timpMediu_AstarStrategy /= 10
    print("\nTimp mediu Random Strategy: ", timpMediu_randomStrategy)
    print("\nTimp mediu BKT Strategy: ", timpMediu_bktStrategy)
    print("\nTimp mediu IDDFS Strategy: ", timpMediu_IddfsStrategy)
    print("\nTimp mediu A* Strategy: ", timpMediu_AstarStrategy)
    print("\n")

    iteratii_randomStrategy /= 10
    iteratii_bktStrategy /= 10
    iteratii_IddfsStrategy /= 10
    iteratii_AstarStrategy /= 10
    print("\nMedie iteratii Random Strategy: ", iteratii_randomStrategy)
    print("\nMedie iteratii BKT Strategy: ", iteratii_bktStrategy)
    print("\nMedie iteratii IDDFS  Strategy: ", iteratii_IddfsStrategy)
    print("\nMedie iteratii A* Strategy: ", iteratii_AstarStrategy)

# Tema1()
Tema2()