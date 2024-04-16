from approxeng.input.selectbinder import ControllerResource
import time
from piservo import Servo

#"sudo pigpiod" needs to be run: this library wont work without it
# exits the program if it was not run
check = input("Have you done sudo pigpiod? y/n")
if check is "y":
    pass
else: 
    print("Please type ", "sudo pigpiod", "before running this code!") #sudo pigpiod needs to be run before this code so that pigpiod is initialised. 
    exit() 


vservo = Servo(12, min_value=0, max_value=180, min_pulse=1.0, max_pulse= 2.0, frequency=50) #GPIO BCM numbers. pin 32
hservo = Servo(13, min_value=0, max_value=180, min_pulse=1.0, max_pulse= 2.0, frequency=50) # horizontal servo pin 33
# library auto checks if values are in range here
vservo.start()
hservo.start()
global servospeedfactor
servospeedfactor = 2.0
vservo_max = 180   #change these after tuning
vservo_min = 0    # change these after tuning
vservo_step = 0.5
vservo_current = 70

hservo_max = 180  #change these after tuning
hservo_min = 0 #change these after tuning
hservo_step = 0.5
hservo_current = 70

delay = float(0.10)

vservo.write(90)  
hservo.write(90)


while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                print("joystick connected")
                slow = joystick['l1']   #turns more slowly if l1 trigger is held
                if slow is not None:
                    servospeedfactor = 0.3
                else: 
                    servospeedfactor = 1.0
                
                moveup = joystick['dup']  #dpad up button
                movedown = joystick['ddown']  #dpad down button
                if moveup is not None:   #checks if up button is held 
                    vservo_current = vservo_current + vservo_step * servospeedfactor #if it is we increase the angle of the servo
                if movedown is not None:  #checks if down button is held
                    vservo_current = vservo_current - vservo_step * servospeedfactor # if it is we decrease the angle of the servo   

                if vservo_current > vservo_max:  #checking that value is in range
                    vservo_current = vservo_max
                    print("Maximum vertical angle reached!")
                if vservo_current < vservo_min:
                    vservo_current = vservo_min
                    print("Minimum vertical angle reached!")

                moveright = joystick['dright']  #dpad right button
                moveleft = joystick['dleft'] #dpad left button
                if moveright is not None:
                    hservo_current = hservo_current + hservo_step * servospeedfactor  #same as before but for horizontal servo
                if moveleft is not None:
                    hservo_current = hservo_current - hservo_step * servospeedfactor

                if hservo_current > hservo_max:
                    hservo_current = hservo_max
                    print("Maximum horizontal angle reached!")
                if hservo_current < hservo_min:
                    hservo_current = hservo_min
                    print("Minimum horizontal angle reached!")
                vservo.write(vservo_current)
                hservo.write(hservo_current)
                
                vturnrate = float(vservo_step/delay)     #DEBUG: prints turn rate of the servo
                print("vertical turn rate:", vturnrate, "degrees per second")
                hturnrate = float(hservo_step/delay)
                print("horizontal turn rate:", hturnrate, "degrees per second")
                print("horizontal:", hservo_current)
                print("vertical;", vservo_current)
                time.sleep(delay)
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        vservo.stop()
        hservo.stop()
        exit()
    except IOError:
        print("Controller not found")

                
                



