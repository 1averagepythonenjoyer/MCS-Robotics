import robot
from time import sleep
r = robot.Robot(max_motor_voltage=12)

Lm_multi = 0.956
Rm_multi = 1
Velocity = 0.97

def move(distance, speed): # speed = 0-1
    time = distance / Velocity / speed
    time = time*(1+(1-speed)*0.15)
    r.motors[0] = 100 * Lm_multi * speed # Left motor
    r.motors[1] = 100 * Rm_multi * speed # Right motor 
    sleep(time)
    r.motors[0] = 0
    r.motors[1] = 0

t = 221
reductionInSpeed = 0.7

def spin(angle):
    spinT = (abs(angle)/t) + 0.05
    while angle > 360:
        angle -= 360
        if angle < 0:
            r.motors[0] = 100*Lm_multi*reductionInSpeed
            r.motors[1] = 100*Rm_multi*reductionInSpeed
        else:
            r.motors[0] = -100*Lm_multi*reductionInSpeed
            r.motors[1] = 100*Rm_multi*reductionInSpeed

        sleep(spinT)
        r.motors[0] = 0
        r.motors[1] = 0

TwistRateRight90 = 147

def Right_twist90(): #twist 90 degrees to grab the box. 
    twist90T = 90/TwistRateRight90
    r.motors[0] = 0
    r.motors[1] = 100*Rm_multi
    sleep(twist90T)
    r.motors[0] = 0
    r.motors[1] = 0

TwistRateRight270 = 162

def Right_twist270():  #twist 270 to go back to starting orientation before picking up the box. We pick up the box, store, then move back
    twist270T = 270/TwistRateRight270
    r.motors[0] = 0
    r.motors[1] = 100*Rm_multi
    sleep(twist270T)
    r.motors[0] = 0
    r.motors[1] = 0

