from approxeng.input.selectbinder import ControllerResource
import mcsmotors 
from time import sleep
while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                print("joystick connected")
                rvalue = joystick['ry']
                lvalue = joystick['ly'] #joystick read values
                lowspeedheld = joystick['r1']   #if r1 is not held, library returns value of None, so we need to check that it is not None
                if lowspeedheld is not None:
                    print("Lowspeed mode!") 
                    LOWSPEED = 0.4
                else:
                    LOWSPEED = 0.0
                mcsmotors.setmotor(rvalue, lvalue)
                print('Left: {}, Right: {}'.format(rvalue, lvalue))
                sleep(0.1)
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        exit()
    except IOError:
        print("Controller not found")
