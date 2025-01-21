import robot
from time import sleep

r = robot.Robot()

t = 127   #Turn rate (degrees per second at full speed)  find this later 
twist =  #Twist rate - speed robot turns 90 degrees with one motor stationary - left and right motor different so robot speed different
v = 0.321  #speed (centimeters per second)

def twist(movement): #Objective of 'twist' is for movement of robot to collect box and also move with the box becuz if robot turns to sharply the box is gonna not be with the robot 
   
    if movement == 'left' or 'box': 
        r.motors[0] = 0 #left motor stationary
        r.motors[1] = 100 #right motor - change if necassary
        sleep(twist)
        r.motors[1] = 0
    elif movement == 'right':
        r.motors[0] = 100 #left motor running - change if necassary
        r.motors[1] = 0 #right motor stationary
        sleep(twist)
        r.motors[0] = 0
    else:
        print('sorry, your commands did not match with our options, please try again or seek help')
        return

def turn(angle): 
    
    turnT = (abs(angle) / t) + 0.05 # Calculate time needed to turn

    while angle > 360:
        angle -= 360
    
    #ADJUST THESE VALUES BELOW DEPENDING ON ACCURACY OF TURN
    if angle < 0:  # rotate left
        r.motors[0] =  -100 # Left motor
        r.motors[1] =  100 # Right motor
    else:  # rotate right
        r.motors[0] =  100 # Left motor
        r.motors[1] = -100  # Right motor

    sleep(turnT)  
    r.motors[0] = 0  
    r.motors[1] = 0

def move(distance):
    if distance > 8:
        distance = 8
        
    if distance = 0:
        exit()

    moveT = (distance / v) + 0.05  # Calculate time needed to move

    if distance > 0:
        r.motors[0] = -100 
        r.motors[1] = - 90
        sleep(moveT) 
        r.motors[0] = 0  
        r.motors[1] = 0

    else:
        r.motors[0] = 100
        r.motors[1] = 90
        sleep(moveT) 
        r.motors[0] = 0  
        r.motors[1] = 0







