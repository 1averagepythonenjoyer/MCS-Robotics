from piservo import Servo
check = input("Have you done sudo pigpiod? y/n")

if check is "y":
    pass
else: 
    print("Please type ", "sudo pigpiod", "before running this code!") #sudo pigpiod needs to be run before this code so that pigpiod is initialised. 
    exit()
vservo = Servo(12, minuseful_value=0, maxuseful_value=180, min_pulse=0.5, max_pulse= 1.0, frequency=50) #GPIO BCM numbers. pin 32
hservo = Servo(13, minuseful_value=0, maxuseful_value=180, min_pulse=0.5, max_pulse= 1.0, frequency=50) # horizontal servo pin 33
# library auto checks if values are in range here
vpos = vservo.read()
hpos = hservo.read()

hresetval = #check
vresetval = #check
def hservo_set(initialdeg):
    hservo.write(initialdeg)
def hservo_move(increment):
    hservo.write(hpos + increment)
def hservo_reset():
    hservo.write(hresetval)

def vservo_set(initialdeg):
    vservo.write(initialdeg)
def vservo_move(increment):
    vservo.write(vpos + increment)
def vservo_reset():
    vservo.write(vresetval)
