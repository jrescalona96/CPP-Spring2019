#John Escalona
#CS3650 Final Project
#Battle Ship Quantum Game using Partial NOT quantum gates

from qiskit import ClassicalRegister, QuantumRegister
from qiskit import QuantumCircuit, execute
from qiskit import Aer, IBMQ
import math
import os

def ask_for_device():
    y_n = input('Real(y)? or Simulator(n): ')
    while (y_n != 'y' and y_n != 'n'):
        y_n = input('Invalid: Real(y)? or Simulator(n):  ')
    if(y_n == 'n'):
        dev = 'qasm_simulator'
    else:
        dev = 'name=\'ibmqx4\''
    return dev

def player_move(p):
    pmove = input('Player ' + str(p+1) + ': ')
    return pmove

def clearScreen():
    if os.name == 'nt':
        os.system('cls')
    else:
        os.system('clear')

#device = ask_for_device()
device = 'qasm_simulator'
try:
    backend = Aer.get_backend(device)
except:
    backend = IBMQ.get_backend(device)
    IBMQ.enable_account('380686068d6ebef96f0331702237f118edafec26806ff0371e9c54e0b50ec28cf0907a2032ddc474daa2550922357e72512d0a50d3b5a3065eeee995aa6bab31')

#Declaration of quantum registers
qc = [] #array of circuits for each player
q = QuantumRegister(4) #quantum register, 5 bit to represent each ship
c = ClassicalRegister(4) #classical register, 5 bits

#declare & initialize circuits
qc.append(QuantumCircuit(q,c)) #player 0
qc.append(QuantumCircuit(q,c)) #player 1

#player ship locations
print('Choose your ship locations(0-3): ')
ship_position = [[],[]] #hold player choices for locations of ships
valid = True
for i in range(2): #choose positions
    print('Player ' + str(i+1) + ': ')
    for j in range(3):
        pos = input('Enter position ' + str(j+1) + ': ')
        if pos not in ship_position[i]:
            ship_position[i].append(pos) #add to array
        else:
            while pos in ship_position[i]:
                pos = input('Error: Enter valid position: ')
            ship_position[i].append(pos) #add to array
            print(ship_position[i])
    clearScreen()

#player moves
bomb = [[],[]] #hold player & res. moves
player = 0 #player 0 starts
counter = 0 #counts total moves
valid_moves = [0,1,2,3] #array of valid moves TODO: Check for valid moves
round = 1
moves = 3

#run game inputs for players
print('\nRound ' + str(round))
while(counter < (moves*2)):
    counter = counter + 1
    pmove = player_move(player) #request player move
    bomb[player].append(pmove)
    if(player == 1):
        player = 0
    else:
        player = 1
    if(counter%2 == 0 and counter < (moves*2)):
        clearScreen()
        round = round + 1
        print('\nRound ' + str(round))

#print Ship Attacks
clearScreen()
print('\nPlayer 1 attacked locations -->' + str(bomb[0]))
print('Player 2 attacked locations -->' + str(bomb[1]))

#print ship locations
print('\nShip locations: ')
for i in range(2): #print player ships
    print('\tPlayer ' + str(i+1) + ': ' + str(ship_position[i]))

#apply gates("bombs") to qubits("ships")
for location in range(moves):
    try:
        index = ship_position[0].index( bomb[1][location] )
    except:
        index = 999999
    fraction = 1/(index + 1) #compute for the angle
    qc[0].u3(fraction*math.pi, 0.0, 0.0, q[ int(bomb[1][location]) ]) # apply gate qubit to a specific angle

for location in range(moves):
    try:
        index = ship_position[1].index( bomb[0][location] )
    except:
        index = 999999
    fraction = 1/(index + 1) #compute for the angle
    qc[1].u3(fraction*math.pi, 0.0, 0.0, q[ int(bomb[0][location]) ]) # apply qubit to a specific angle

#measure qubits
for player in range(2):
    for qubit in range(4):
        qc[player].measure(q[qubit], c[qubit])

#run in Quantum computer
shots = 2000 #number of executions
job = execute(qc, backend, shots=shots) #run job
result = [[],[]] #dictionary of results
pboard = [[],[]] #hold ship status after attacks

#get and print results
print('\nQuantum Calculation Results:')
for player in range(2):
    result[player] = job.result().get_counts(qc[player]) #dictionary obj
    print('\tPlayer ' + str(player+1) + ' --> '+ str(result[player]))
    for res in result[player]:
        pboard[player].append(res)

#print results
print('\nPercent Health of Ships:')
ship_health = [[100,100,100,100],[100,100,100,100]]
for player in range(2):
    for board_status in pboard[player]:
        prob = (result[player].get(board_status))/shots #compute probability for each result
        ship_num = 0
        for sh in ship_health[player]:
            while(ship_num < 4):
                ship_health[player][ship_num] = sh - ((float(board_status[int(ship_num)]) * prob)*100) 
                if(sh < 1): #if ship health is negative
                    ship_health[player][ship_num] = 0.0
                ship_num = ship_num + 1
    print('\tPlayer ' + str(player+1) + ' ships health:' + str(ship_health[player]))

#declare winner
p1_total = sum(ship_health[0],0)
p2_total = sum(ship_health[1],0)
if(p2_total-p1_total) < 0:
    print('\nPlayer 1 Wins!')
else:
    print('\nPlayer 2 Wins!')
print("\n\nEndgame..\n")

#print Quantum Circuit
print('\nPlayer 1 circuit:')
print(qc[0])
print('Player 2 circuit:')
print(qc[1])