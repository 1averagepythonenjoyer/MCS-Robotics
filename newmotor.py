from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install
#DOUBLE CHECK LEFT AND RIGHT MOTOR A AND B
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
#setting pwm pins and starting them up
#left
PWM_ENA = 13  
GPIO.setup(PWM_ENA, GPIO.OUT)
PWMa = GPIO.PWM(PWM_ENA, 100)
PWMa.start(0)
#right
PWM_ENB = 15
GPIO.setup(PWM_ENB, GPIO.OUT)
PWMb= GPIO.PWM(PWM_ENB, 100)
PWMb.start(0)

#h bridge logic 
#currently set to forward motion
IN1= 19      #INPUT1 and INPUT2 logic pins on L298 control direction of left motor i believe
GPIO.setup(IN1, GPIO.OUT) #double check which way round it is
GPIO.output(IN1, GPIO.HIGH)
IN2 = 21
GPIO.setup(IN2, GPIO.OUT)
GPIO.output(IN2, GPIO.LOW)
IN3 = 16      #INPUT3 and INPUT4 logic pins control right motor
GPIO.setup(IN3, GPIO.OUT)
GPIO.output(IN3, GPIO.HIGH)
IN4 = 18
GPIO.setup(IN4, GPIO.OUT)
GPIO.output(IN4, GPIO.LOW)

#RightMotor and LeftMotor corrections
RMcorrection = 1.0 #to offset drift of motors (1 motor spinning faster than the other: left motor spins faster than the right I think)
LMcorrection = 0.8
LOWSPEED = 0.4 #for more precise movement.

def mix(yaw, throttle, max_power=100):
    leftpower = (LMcorrection * throttle) - yaw #if yaw is negative turn left so take away power from left motor
    rightpower = (RMcorrection * throttle) + yaw # if yaw is positive, turn right, so add power to right motor
    scale = float(max_power) / max(1, abs(leftpower), abs(rightpower))

    l = int(leftpower * scale)
    r = int(rightpower * scale)
    #if throttle >= 0: #hbridge forward
        #GPIO.output(IN1, GPIO.HIGH)
        #GPIO.output(IN2, GPIO.LOW)
        #GPIO.output(IN3, GPIO.HIGH)
        #GPIO.output(IN4, GPIO.LOW)
    #if throttle < 0: #hbridge backward
        #GPIO.output(IN1, GPIO.LOW)
        #GPIO.output(IN2, GPIO.HIGH)
        #GPIO.output(IN3, GPIO.LOW)
        #GPIO.output(IN4, GPIO.HIGH)
    #PWMa.ChangeDutyCycle(l)
    #PWMb.ChangeDutyCycle(r)
#not sure if needed: will test. 

def remotecontrol():
    while True:
        try:
            with ControllerResource() as joystick:
                while joystick.connected:
                    global lvalue  #left joystick analogue value to control left motor 
                    global rvalue  #right joystick analogue value to control right motor
                    global lowspeedheld

                    lvalue = joystick.['l']
                    rvalue = joystick.['r']
                    if lvalue <= 0:                 
                        GPIO.output(IN1, GPIO.LOW)
                        GPIO.output(IN2, GPIO.HIGH)
                        PWMa.ChangeDutyCycle(abs(lvalue))
                    elif lvalue > 0:
                        GPIO.output(IN1, GPIO.HIGH)
                        GPIO.output(IN2, GPIO.LOW)
                        PWMa.ChangeDutyCycle(lvalue)
                    
                    if rvalue <= 0:                 
                        GPIO.output(IN3, GPIO.LOW)
                        GPIO.output(IN4, GPIO.HIGH)
                        PWMb.ChangeDutyCycle(abs(rvalue))
                    elif rvalue > 0:
                        GPIO.output(IN3, GPIO.HIGH)
                        GPIO.output(IN4, GPIO.LOW)
                        PWMa.ChangeDutyCycle(lvalue)

                    lowspeedheld = joystick.['r2'] # hold r2 trigger if we want slower movement
                    if lowspeedheld is not None: #approxeng API returns None if the button is not held
                        PWMa.ChangeDutyCycle(lvalue * LOWSPEED)
                        PWMb.ChangeDutyCycle(rvalue * LOWSPEED)
                    else:
                        PWMa.ChangeDutyCycle(lvalue)
                        PWMb.ChangeDutyCycle(rvalue)            
        except IOError:
            print('Unable to find any joysticks')
            sleep(1.0)
        except KeyboardInterrupt:
            print("Keyboard interrupt detected")
            exit()
