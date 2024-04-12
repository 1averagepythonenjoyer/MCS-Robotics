from approxeng.input.selectbinder import ControllerResource
import time
import mcsservos
while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                print("joystick connected")
                hservo_val = joystick['rx']
                hservo_final = hservo_val 
                vservo_val = joystick['ly']
                vservo_final = vservo_val 
                #write values to servos 
                mcsservos.hservo_move(hservo_final)
                mcsservos.vservo_move(vservo_final)
      
