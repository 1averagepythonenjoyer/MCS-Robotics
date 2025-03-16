import robot
from time import sleep
r = robot.Robot(max_motor_voltage=12)
Lm_multi = 0.956
Rm_multi = 1
Velocity = 0.97

def move(distance, speed): # speed = 0-1
r.motors[0] = 100 * Lm_multi * speed # Left motor
r.motors[1] = 100 * Rm_multi * speed # Right motor
time = distance / Velocity / speed
time = time*(1+(1-speed)*0.15)
sleep(time)
r.motors[0] = 0
r.motors[1] = 0

move(2,1)


t = 221
reductionInSpeed = 0.7
def spin(angle):
spinT = (abs(angle)/t) + 0.05
while angle > 360:
angle -= 360
if angle < 0:
r.motors[0] = 100*Lm_multi*reductionInSpeed
t.motors[1] = 100*Rm_multi*reductionInSpeed
else:
r.motors[0] = -100*Lm_multi*reductionInSpeed
r.motors[1] = 100*Rm_multi*reductionInSpeed

sleep(spinT)
r.motors[0] = 0
r.motors[1] = 0

spin(360)

TwistRateRight90 = 147

def Right_twist90():
r.motors[0] = 0
r.motors[1] = 100*Rm_multi
sleep(90/TwistRateRight90)
r.motors[0] = 0
r.motors[1] = 0

TwistRateRight270 = 162

def Right_twist270():
r.motors[0] = 0
r.motors[1] = 100*Rm_multi
sleep(270/TwistRateRight270)
r.motors[0] = 0
r.motors[1] = 0

Right_twist270()
sleep(1)
Right_twist90()

