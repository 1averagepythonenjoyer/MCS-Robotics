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

TwistRateRight = 142
TwistRateLeft = 147
twist_multi = 0.5


def twist(angle): #twist 90 degrees to grab the box. 
    #positive angle: turn right
    #negative angle: turn left
    if angle < 0: 
        twistTRight = abs((angle/TwistRateRight)/twist_multi)
        r.motors[0] = 0
        r.motors[1] = 100*Rm_multi*twist_multi
        sleep(twistTRight)
        r.motors[0] = 0
        r.motors[1] = 0
    elif angle > 0: 
        twistTLeft = abs((angle/TwistRateLeft)/twist_multi)
        r.motors[0] = 100*Lm_multi*twist_multi  #need to tune for twisting left
        r.motors[1] = 0
        sleep(twistTLeft)
        r.motors[0] = 0
        r.motors[1] = 0
    else:
        pass
