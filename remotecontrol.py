from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install
from mcsmotors import *
def checkvalues():
    print('Left: {}, Right: {}'.format(rvalue, lvalue))
    print('Left duty: {}, Right duty: {}'.format(rmotor, lmotor))
    sleep(0.1)
while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                    rvalue = joystick['ry']
                    lvalue = joystick['ly'] #joystick read values
                    lowspeedheld = joystick['r1']   #if r1 is not held, library returns value of None, so we need to check that it is not None
                    if lowspeedheld is not None: 
                        LOWSPEED = 0.4
                    else:
                        LOWSPEED = 0.0
                    setmotor(rvalue, lvalue)
                    checkvalues()                    
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        exit()
    except IOError:
        print("Controller not found")
        sleep(1)
