from time import sleep
import RPi.GPIO as GPIO # remember to install
from approxeng.input.selectbinder import ControllerResource # remember to install

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)

PWMaenable = 14
GPIO.setup(PWMaenable, GPIO.OUT)
PWMa= GPIO.PWM(PWMaenable, 100)
PWMa.start(0)

PWMbenable = 15
GPIO.setup(PWMbenable, GPIO.OUT)
PWMb= GPIO.PWM(PWMbenable, 100)
PWMb.start(0)

input1 = 16           
GPIO.setup(input1, GPIO.OUT)
GPIO.output(input1, GPIO.HIGH)

input2 = 18
GPIO.setup(input2, GPIO.OUT)
GPIO.output(input2, GPIO.HIGH)


def set_speed(power_left, power_right):
    r_motor= map_range(power_right,-100,100,0,100) # add gpio out analog with duty cycle 0-100
    l_motor= map_range(power_left,-100,100,0,100)
    print('Left: {}, Right: {}'.format(power_left, power_right)) # troubleshhoting
    print('Left duty percent: {}, Right duty percent: {}'.format(l_motor, r_motor))
    PWMa.ChangeDutyCycle(l_motor) 
    PWMb.ChangeDutyCycle(r_motor)
    sleep(0.1)

def mix(yaw, throttle, max_power=100):
    left = throttle + yaw
    right = throttle - yaw
    scale = float(max_power) / max(1, abs(left), abs(right))
    return int(left * scale), int(right * scale)

def map_range(x, in_min, in_max, out_min, out_max):
  return (x - in_min) * (out_max - out_min) // (in_max - in_min) + out_min

while True:
    try:
        with ControllerResource() as joystick:
          x_axis, y_axis = joystick['lx','ly']
          power_left, power_right = mix(yaw=x_axis, throttle=y_axis)
          set_speed(power_left, power_right)
    except IOError:
        print('Unable to find any joysticks')
        sleep(1.0)
