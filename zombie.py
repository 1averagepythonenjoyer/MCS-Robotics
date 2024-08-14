###########################ALL SETUP DOWN HERE####################################
from approxeng.input.selectbinder import ControllerResource
import time
from piservo import Servo
import RPi.GPIO as GPIO 
import blinkylightsbcm

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)
blinkylightsbcm.blinkylights_on()

#"sudo pigpiod" needs to be run: this library wont work without it
# exits the program if it was not run
check = input("Have you done sudo pigpiod? y/n")
if check == "y":
    pass
else: 
    print("Please type ", "sudo pigpiod", "before running this code!") #sudo pigpiod needs to be run before this code so that pigpiod is initialised. 
    exit() 

vservo = Servo(12, min_value=0, max_value=180, min_pulse=1.0, max_pulse= 2.0, frequency=50) #GPIO BCM numbers. pin 32
hservo = Servo(13, min_value=0, max_value=180, min_pulse=0.8, max_pulse= 3.0, frequency=50) # horizontal servo pin 33
# library auto checks if values are in range here
vservo.start()
hservo.start()
def startreset():
    check2 = input("Have you spun the servos into the guns straight position beforehand? y/n")
    if check2 == "y":
        vservo.write(70)
        hservo.write(30)
    else:
        print("please ensure that you have otherwise the gun mechanism may break")
        exit()

startreset()

global servospeedfactor
servospeedfactor = 3.0
vservo_max = 150   #change these after tuning
vservo_min = 30    # change these after tuning
vservo_step = 1.5
vservo_current = vservo.read()

hservo_max = 120  #change these after tuning
hservo_min = 20 #change these after tuning
hservo_step = 1.5
hservo_current = hservo.read()

delay = float(0.10)

gun_pin = 21
GPIO.setup(gun_pin, GPIO.OUT)
GPIO.output(gun_pin, GPIO.HIGH)
#########################################ALL SETUP ABOVE#################################################

while True:
    try:
        with ControllerResource() as joystick:
            while joystick.connected:
                ############################SERVO CONTROL############################
                #SERVO SLOW MODE BUTTON SETUP
                slow = joystick['l1']   #turns more slowly if l1 trigger is held
                if slow is not None:
                    servospeedfactor = 0.3
                else: 
                    servospeedfactor = 1.0
                #VERTICAL AXIS CONTROL
                moveup = joystick['dup']  #dpad up button
                movedown = joystick['ddown']  #dpad down button
                if moveup is not None:   #checks if up button is held 
                    vservo_current = vservo_current + vservo_step * servospeedfactor #if it is we increase the angle of the servo
                if movedown is not None:  #checks if down button is held
                    vservo_current = vservo_current - vservo_step * servospeedfactor # if it is we decrease the angle of the servo   
                #CHECK CORRECT RANGE
                if vservo_current > vservo_max:  #checking that value is in range
                    vservo_current = vservo_max
                    print("Maximum vertical angle reached!")
                if vservo_current < vservo_min:
                    vservo_current = vservo_min
                    print("Minimum vertical angle reached!")
                #HORIZONTAL AXIS CONTROL
                moveright = joystick['dright']  #dpad right button
                moveleft = joystick['dleft'] #dpad left button
                if moveright is not None:
                    hservo_current = hservo_current - hservo_step * servospeedfactor  #same as before but for horizontal servo
                if moveleft is not None:
                    hservo_current = hservo_current + hservo_step * servospeedfactor
                #CHECK CORRECT RANGE
                if hservo_current > hservo_max:
                    hservo_current = hservo_max
                    print("Maximum horizontal angle reached!")
                if hservo_current < hservo_min:
                    hservo_current = hservo_min
                    print("Minimum horizontal angle reached!")
                
                vservo.write(vservo_current)
                hservo.write(hservo_current)
                print("horizontal:", hservo_current)
                print("vertical;", vservo_current)
                #gun fire control
                fire = joystick['r2']
                if fire is not None:
                    print("gun firing")
                    GPIO.output(gun_pin, GPIO.LOW)
                    time.sleep(0.1)
                    GPIO.output(gun_pin, GPIO.HIGH)
                    time.sleep(2)

                time.sleep(delay)
                
                
                    
    except KeyboardInterrupt:
        print("Keyboard interrupt detected: stopping")
        vservo.stop()
        hservo.stop()
        blinkylightsbcm.blinkylights_off()
        #GPIO.cleanup()
        exit()
    except IOError:
        print("Controller not found")
        blinkylightsbcm.blinkylights_off()
       

                
                



