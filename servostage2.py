from approxeng.input.selectbinder import ControllerResource
import time
import mcsservos
import blinkylights
blinkylights.blinkylights_on()
global servospeedfactor
servospeedfactor = 1.0

while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                print("joystick connected")
                hservo_val = joystick['rx']
                hservo_final = hservo_val * servospeedfactor
                vservo_val = joystick['ly']
                vservo_final = vservo_val *servospeedfactor
                mcsservos.hservo_move(hservo_final)
                mcsservos.vservo_move(vservo_final)
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        blinkylights.blinkylights_off()
        exit()
    except IOError:
        print("Controller not found")
        blinkylights.blinkylights_off()
                
                



