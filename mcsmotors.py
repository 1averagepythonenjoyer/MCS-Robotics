from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install
from mcsmotors import *

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)

GPIO.cleanup()

PWM_ENA = 13  
PWM_ENB = 15

GPIO.setup(PWM_ENA, GPIO.OUT)
GPIO.setup(PWM_ENB, GPIO.OUT)

PWMa = GPIO.PWM(PWM_ENA, 100)
PWMb= GPIO.PWM(PWM_ENB, 100)

PWMa.start(0)
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

RMcorrection = 1.0 #to offset drift of motors (1 motor spinning faster than the other: left motor spins faster than the right I think)
LMcorrection = 0.8
LOWSPEED = 0.4

def remotecontrol():
    try:
        while True:
            try:
                with ControllerResource as joystick:
                    while joystick.connected:
                    global lvalue
                    global rvalue 
                    global lmotor
                    global rmotor
                    global lowspeedheld
                    lvalue = joystick['ly'] #joystick read values
                    rvalue = joystick['ry']
                    #check hbridge for left motor
                    if lvalue < 0:     #backward
                        GPIO.output(IN1, GPIO.LOW)
                        GPIO.output(IN2, GPIO.HIGH)
                    elif lvalue >= 0:    #forward/neutral
                        GPIO.output(IN1, GPIO.HIGH)
                        GPIO.output(IN2, GPIO.LOW)
                    #check hbridge for right motor
                    if rvalue < 0:  #backward
                        GPIO.output(IN3, GPIO.LOW)
                        GPIO.output(IN4, GPIO.HIGH)
                    elif rvalue >= 0:    #forward/neutral
                        GPIO.output(IN3, GPIO.HIGH)
                        GPIO.output(IN4, GPIO.LOW)

                    lmotor = map_range(lvalue,0,100,0,100)   #maps joystick values to actual pwm values
                    rmotor = map_range(rvalue,0,100,0,100)    #need to check deadzones

                    lowspeedheld = joystick['r1']   #if r1 is not held, library returns value of None, so we need to check that it is not None
                    if lowspeedheld is not None: 
                        PWMa.ChangeDutyCycle(lmotor * LOWSPEED)
                        PWMb.ChangeDutyCycle(rmotor * LOWSPEED)
                    else:
                        PWMa.ChangeDutyCycle(lmotor)
                        PWMb.ChangeDutyCycle(rmotor)
            except IOError:
                print("No Controller found")
                sleep(1)
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        exit()




        
    


    
