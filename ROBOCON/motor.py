import robot
from time import sleep

r = robot.Robot()


t = 127   #Turn rate (degrees per second at full speed)  find this later 
twist_angle =  #Twist rate - speed robot turns 90 degrees with one motor stationary
v = 0.321  #speed (centimeters per second)

LMC = #left motor compensation from 0 to 1 not including 0 but include 1
if LMC <= 0 or LMC > 1:
   exit()

RMC = #right motor compensation from 0 to 1 not including 0 but including 1
if RMC <= 0 or RMC > 1:
   exit()

def twist(angle): #Objective of 'twist' is for movement of robot to collect box and also move with the box becuz if robot turns to sharply the box is gonna not be with the robot 
   
   while angle >= 360:
      angle -= 360

   if angle == 0:
      exit()

   if angle > 0:
      r.motors[0] = 100 * LMC
      r.motors[1] = 0
   else:
      r.motors[0] = 0
      r.motors[1] = 100 * RMC

   sleep((angle / 90) * twist_angle)

def spin(angle): 
    
   spinT = (abs(angle) / t) + 0.05 # Calculate time needed to turn

   while angle > 360:
      angle -= 360

   if angle < 0:  # rotate left
      r.motors[0] =  100 * LMC # Left motor
      r.motors[1] =  -100 * RMC # Right motor
   else:  # rotate right
      r.motors[0] =  -100 * LMC # Left motor
      r.motors[1] = 100 * RMC  # Right motor

   sleep(spinT)  
   r.motors[0] = 0  
   r.motors[1] = 0

def move(distance):
   if distance > 2:
      distance = 2
      
   if distance < -2:
      distance = -2  
      
   if distance = 0:
      exit()

   moveT = (distance / v) + 0.05  # Calculate time needed to move

   if distance > 0:
      r.motors[0] = 100 
      r.motors[1] = 100
   else:
      r.motors[0] = -100
      r.motors[1] = -100
       
   sleep(moveT) 
   r.motors[0] = 0  
   r.motors[1] = 0
  
  
t = 127   #Turn rate (degrees per second at full speed)  find this later
twist = 1 #Twist rate - speed robot turns 90 degrees with one motor stationary - left and right motor different so robot speed different
v = 0.321  #speed (centimeters per second)

LMC =  #Left motor compensation values from 0 to 1
if LMC <= 0 and LMC > 100:
    exit()
RMC =  #Right motor compensation values from 0 to 1
if RMC <= 0 and RMC > 100:
    exit()

def twist(angle): #Objective of 'twist' is for movement of robot to collect box and also move with the box becuz if robot turns to sharply the box is gonna not be with the robot
#still needs to be tunned
    turnT = 1.8* (abs(angle) / t) + 0.05 # Calculate time needed to turn

    if angle < 0:  # rotate left?  This turns left motor full ON forward, how come this is twist left?
        r.motors[0] =  -100 * LMC # Left motor
        r.motors[1] =  0 # Right motor
    else:  # rotate right
        r.motors[0] =  0 # Left motor
        r.motors[1] = -100 * RMC # Right motor

    sleep(turnT)  
    r.motors[0] = 0  
    r.motors[1] = 0

def spin(angle):
   
    turnT = (abs(angle) / t) + 0.05 # Calculate time needed to turn

    while angle > 360:
        angle -= 360
   
   #somewhere here we need to update position of the robot

    #ADJUST THESE VALUES BELOW DEPENDING ON ACCURACY OF TURN
    if angle < 0:  # rotate left
        r.motors[0] =  -100 * LMC # Left motor
        r.motors[1] =  100 * RMC  # Right motor
    else:  # rotate right
        r.motors[0] =  100 * LMC # Left motor
        r.motors[1] = -100 * RMC # Right motor

    sleep(turnT)  
    r.motors[0] = 0  
    r.motors[1] = 0

def move(distance):
    if distance > 2: # camera can only see objects 2m ahead
        distance = 2 
    if distance < -2:
        distance = -2

   #somewhere here we need to update position of the robot

    if distance == 0:
        exit()

    moveT = (distance / v) + 0.05  # Calculate time needed to move

    if distance > 0:
        r.motors[0] = -100 * LMC
        r.motors[1] = - 100 * RMC
        sleep(moveT)
        r.motors[0] = 0  
        r.motors[1] = 0

    else:
        r.motors[0] = 100 * LMC
        r.motors[1] = 100 * RMC
        sleep(moveT)
        r.motors[0] = 0  
        r.motors[1] = 0
