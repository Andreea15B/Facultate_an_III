DEPTH_MAX = 10
import copy
class State:
    def __init__(self, board, turn):
        self.board = board
        self.turn = turn
    def isFinal(self):
        first_row = [i for i in self.board[0]]
        numberC = first_row.count(2)
        last_row = [i for i in self.board[3]]
        numberP = last_row.count(1)
        if numberC == 4 or numberP == 4:
            return True
        return False
    def __str__(self):
        return f'board: {self.board}, turn: {self.turn}'

def heuristic(state):
    sumaP = 0
    sumaC = 0
    for i in range(0,4):
        for j in range(0,4):
            if state.board[i][j] == 1:
                sumaP += i
    for i in range(0,4):
        for j in range(0,4):
            if state.board[i][j] == 2:
                sumaC += 3-i
    return sumaC - sumaP

def printBoard(state):
    if state.turn == 0:
        print("Initial board: ")
    elif state.turn == 1:
        print("Your move - current board: ")
    else:
        print("Calculator move - current board: ")
    for line in state.board:
        print(line)

def checkInput(board, from_i, from_j, to_i, to_j):
    if from_j > 3 or from_i > 3 or to_j > 3 or to_i > 3 or from_j < 0 or from_i < 0 or to_j < 0 or to_i < 0:
        return False
    if board[to_i][to_j] != 0:
        return False
    if board[to_i][to_j] < 0 or board[to_i][to_j] > 3:
        return False
    if board[from_i][from_j] < 0 or board[from_i][from_j] > 3 or board[from_i][from_j] == 0:
        return False
    if from_i == to_i and from_j == to_j:
        return False
    if to_i - from_i > 1:
        return False
    if to_j - from_j > 1:
        return False
    return True

def getPersonMove(state):
    move_from_i = int(input("Enter from - row:"))
    move_from_j = int(input("Enter from - column:"))
    move_to_i = int(input("Enter to - row: "))
    move_to_j = int(input("Enter to - column: "))

    while checkInput(state.board, move_from_i, move_from_j, move_to_i, move_to_j) == False:
        print("\nWrong move! Try again:")
        move_from_i = int(input("Enter from - row:"))
        move_from_j = int(input("Enter from - column:"))
        move_to_i = int(input("Enter to - row: "))
        move_to_j = int(input("Enter to - column: "))

    state.board[move_from_i][move_from_j] = 0
    state.board[move_to_i][move_to_j] = 1

    return state

def getAllStates(state, who):
    states = []
    dirrections_i = [-1, -1, -1, 0, 1, 1, 1, 0]
    dirrections_j = [-1, 0, 1, 1, 1, 0, -1, -1]
    for row in range(len(state.board)):
        for column in range(len(state.board[row])):
            if state.board[row][column] == who:
                for index in range(0,8):
                    if row + dirrections_i[index] >= 0 and row + dirrections_i[index] <= 3 \
                            and column + dirrections_j[index] >= 0 and column + dirrections_j[index] <= 3 \
                            and state.board[row + dirrections_i[index]][column + dirrections_j[index]] != who \
                            and state.board[row + dirrections_i[index]][column + dirrections_j[index]] != 3-who:
                        newBoard = copy.deepcopy(state.board)
                        newBoard[row + dirrections_i[index]][column + dirrections_j[index]] = who
                        newBoard[row][column] = 0
                        newState = State(newBoard, 3-who)
                        states.append(newState)
    return states

# def evaluateCalculatorMove(current_state):
#     states = getAllStates(current_state)
#     values = []
#     maxim = 0
#     for state in states:
#         value = heuristic(state)
#         if value >= 0:  # avantaj C
#             values.append(value)
#             if value >= maxim:
#                 maxim = value
#                 next_state = state
#     return next_state

def evaluateCalculatorMove(current_state):
    states = getAllStates(current_state, 2)
    values = []
    maxim = 0
    for state in states:
        # value = minimax(DEPTH_MAX, 'max', state, 1)
        value = alphaBeta(DEPTH_MAX, 'max', state, 1, 999, 999)
        if value >= 0:  # avantaj C
            values.append(value)
            if value >= maxim:
                maxim = value
                next_state = state
    return next_state

def alphaBeta(depth_max, level_type, state, who, alpha, beta):
    states = getAllStates(state, 3 - who)
    if level_type == 'max':
        val = 999
        for state in states:
            if depth_max == 1:
                childValue = heuristic(state)
            else:
                who = 3 - who
                childValue = alphaBeta(depth_max - 1, 'min', state, who, alpha, beta)
            val = max(val, childValue)
            alpha = max(alpha, val)
            if beta <= alpha:
                break
        return val
    if level_type == 'min':
        val = -999
        for state in states:
            if depth_max == 1:
                childValue = heuristic(state)
            else:
                who = 3 - who
                childValue = alphaBeta(depth_max - 1, 'max', state, who, alpha, beta)
            val = min(val, childValue)
            beta = min(beta, val)
            if beta <= alpha:
                break
        return val

def minimax(depth_max, level_type, state, who):
    states = getAllStates(state, 3-who)
    if level_type == 'max':
        val = 999
        for state in states:
            if depth_max == 1:
                childValue = heuristic(state)
            else:
                who = 3-who
                childValue = minimax(depth_max-1, 'min', state, who)
            val = max(val, childValue)
        return val
    if level_type == 'min':
        val = -999
        for state in states:
            if depth_max == 1:
                childValue = heuristic(state)
            else:
                who = 3 - who
                childValue = minimax(depth_max - 1, 'max', state, who)
            val = min(val, childValue)
        return val

def play():
    # 1 - person, 2 - calculator
    matrix = [
        [1, 1, 1, 1],
        [0, 0, 0, 0],
        [0, 0, 0, 0],
        [2, 2, 2, 2]
    ]
    state = State(matrix, 0)
    printBoard(state)
    while state.isFinal() == False:
        # person move:
        state.turn = 1
        getPersonMove(state)
        printBoard(state)
        # calculator move:
        state = evaluateCalculatorMove(state)
        state.turn = 2
        printBoard(state)

play()