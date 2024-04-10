from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install
from mcsmotors import *
while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                    rvalue = joystick['ry']
                    lvalue = joystick['ly'] #joystick read values
                    if rvalue < 0:  #backward
                        GPIO.output(IN1, GPIO.LOW)
                        GPIO.output(IN2, GPIO.HIGH)
                    elif rvalue >= 0:    #forward/neutral
                        GPIO.output(IN1, GPIO.HIGH)
                        GPIO.output(IN2, GPIO.LOW)
                    #check hbridge for left motor
                    if lvalue < 0:     #backward
                        GPIO.output(IN3, GPIO.LOW)
                        GPIO.output(IN4, GPIO.HIGH)
                    elif lvalue >= 0:    #forward/neutral
                        GPIO.output(IN3, GPIO.HIGH)
                        GPIO.output(IN4, GPIO.LOW)
                    #check hbridge for right motor
                    rmotor = map_range(rvalue,0,100,0,100)    #need to check deadzones
                    lmotor = map_range(lvalue,0,100,0,100)   #maps joystick values to actual pwm values
                    print('Left: {}, Right: {}'.format(rvalue, lvalue))
                    print('Left duty: {}, Right duty: {}'.format(rmotor, lmotor))
                    lowspeedheld = joystick['r1']   #if r1 is not held, library returns value of None, so we need to check that it is not None
                    
                    if lowspeedheld is not None: 
                        PWMa.ChangeDutyCycle(lmotor * LOWSPEED)
                        PWMb.ChangeDutyCycle(rmotor * LOWSPEED)
                    else:
                        PWMa.ChangeDutyCycle(lmotor)
                        PWMb.ChangeDutyCycle(rmotor)
                
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        exit()
    except IOError:
        print("Controller not found")
        sleep(1)
