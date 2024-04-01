from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

PWM_ENA = 13  
GPIO.setup(PWM_ENA, GPIO.OUT)
PWMa= GPIO.PWM(PWM_ENA, 100)
PWMa.start(0)

PWM_ENB = 15
GPIO.setup(PWM_ENB, GPIO.OUT)
PWMb= GPIO.PWM(PWM_ENB, 100)
PWMb.start(0)

#h bridge logic 
#currently set to forward motion
IN1= 19      #INPUT1
GPIO.setup(IN1, GPIO.OUT)
GPIO.output(IN1, GPIO.HIGH)
IN2 = 21
GPIO.setup(IN2, GPIO.OUT)
GPIO.output(IN2, GPIO.LOW)
IN3 = 16
GPIO.setup(IN3, GPIO.OUT)
GPIO.output(IN3, GPIO.HIGH)
IN4 = 18
GPIO.setup(IN4, GPIO.OUT)
GPIO.output(IN4, GPIO.LOW)


#RightMotor and LeftMotor corrections
RMcorrection = 1.0 #to offset drift of motors (1 motor spinning faster than the other: left motor spins faster than the right I think)
LMcorrection = 0.8
LOWSPEEDMODE = 1.0 #INITIALLY SET TO 1


def mix(yaw, throttle, max_power=100):
    leftpower = (LMcorrection * throttle) - yaw #if yaw is negative turn left so take away power from left motor
    rightpower = (RMcorrection * throttle) + yaw # if yaw is positive, turn right, so add power to right motor
    scale = float(max_power) / max(1, abs(leftpower), abs(rightpower))
    return int(leftpower * scale), int(rightpower * scale)

def map_range(x, in_min, in_max, out_min, out_max): #nicky's function. Not entirely sure what it does. 
    return (x - in_min) * (out_max - out_min) // (in_max - in_min) + out_min

def set_speed(power_left, power_right):
    if y >= 0:
        #forward h bridge 
        
        global r_motor
        global l_motor
        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
        GPIO.output(IN3, GPIO.HIGH)
        GPIO.output(IN4, GPIO.LOW)
        
        r_motor= map_range(power_right,0,100,0,100) # add gpio out analog with duty cycle 0-100
        l_motor= map_range(power_left,0,100,0,100)
    
    elif y < 0:

        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
        GPIO.output(IN3, GPIO.LOW)
        GPIO.output(IN4, GPIO.HIGH)

        r_motor= map_range(power_right,0,100,0,100)
        l_motor= map_range(power_left,0,100,0,100)

    print('Left: {}, Right: {}'.format(power_left, power_right))
    print('Left duty percent: {}, Right duty percent: {}'.format(l_motor, r_motor))
    PWMa.ChangeDutyCycle(LOWSPEEDMODE * l_motor) 
    PWMb.ChangeDutyCycle(LOWSPEEDMODE * r_motor)
    sleep(0.1)

def set_turn(): #not done yet. Not sure how to do this part
    if x >= 0:
        #hbridge so it turns right
        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
        GPIO.output(IN3, GPIO.HIGH)
        GPIO.output(IN4, GPIO.LOW)
    
    r_motor = map_range(power_right,0,100,0,100) # add gpio out analog with duty cycle 0-100
    l_motor = map_range(power_left,0,100,0,100)
    
    elif x < 0: 
        #hbridge so it turns left
        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
        GPIO.output(IN3, GPIO.LOW)
        GPIO.output(IN4, GPIO.HIGH)
    

def remote_control():
    while True:
        try:
            with ControllerResource() as joystick:
                while joystick.connected:
                    global lowspeedmodeheld
                    global y 
                    global x
                    y = joystick['ly'] # Only the left-joystick's y value is being read: left joystick controls forward/backward speed
                    x = joystick['rx'] # Only right joystick's x value is being read: right joystick controls left and right yaw
                    
                    lowspeedmodeheld = joystick['r2'] # HOLD R2 TRIGGER FOR LOWER SPEED
                    if lowspeedmodeheld is not None:
                        LOWSPEEDMODE = 0.4
                    else: 
                        LOWSPEEDMODE = 1.0
                    
                    power_left, power_right = mix(yaw=x, throttle=y)
                    print("x =", x)
                    print("y =", y)
                    set_speed(power_left, power_right)
                    set_turn(power_left, power_right)
        except IOError:
            print('Unable to find any joysticks')
            sleep(1.0)
        except KeyboardInterrupt:
            print("Keyboard interrupt detected")
            exit()
remote_control()
