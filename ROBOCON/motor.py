import robot
from time import sleep

r = robot.Robot()


t = 127   #Turn rate (degrees per second at full speed)  find this later 
twist_angle =  #Twist rate - speed robot turns 90 degrees with one motor stationary
#^what are we doing with this... the code won't compile if we leave it empty.

v = 0.321  #speed (metres per second)

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
