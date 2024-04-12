from approxeng.input.selectbinder import ControllerResource
import mcsmotors
from time import sleep
import blinkylights
blinkylights.blinkylights_on()
while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                print("joystick connected")
                rvalue = joystick['rx']
                lvalue = joystick['ly'] #joystick read values
                #lowspeedheld = joystick['r1']   #if r1 is not held, library returns value of None, so we need to check that it is not None
                #if lowspeedheld is not None:

                if joystick['r1']:
                    print("Lowspeed mode!")
                    LOWSPEED = 0.4
                else:
                    LOWSPEED = 1.0
                mcsmotors.yawthrottle(rvalue,lvalue, LOWSPEED)
#                rvalue = rvalue *100
#                lvalue = lvalue *100
#                mcsmotors.setmotor(rvalue*LOWSPEED, lvalue*LOWSPEED)
                #print('Left: {}, Right: {}'.format(lvalue, rvalue))
                sleep(0.05)
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        blinkylights.blinkylights_off()
        exit()
    except IOError:
        print("Controller not found")
        blinkylights.blinkylights_off()
