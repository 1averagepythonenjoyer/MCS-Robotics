from time import sleep
from approxeng.input.selectbinder import ControllerResource

def set_speed(power_left, power_right):
    r_motor= map_range(power_right,-100,100,0,255) # add gpio out analog
    l_motor= map_range(power_left,-100,100,0,255)
    print('Left: {}, Right: {}'.format(power_left, power_right))
    print('Left pwm: {}, Right pwm: {}'.format(l_motor, r_motor))
    sleep(0.1)

def mix(yaw, throttle, max_power=100):
    left = throttle + yaw
    right = throttle - yaw
    scale = float(max_power) / max(1, abs(left), abs(right))
    return int(left * scale), int(right * scale)

def map_range(x, in_min, in_max, out_min, out_max):
  return (x - in_min) * (out_max - out_min) // (in_max - in_min) + out_min

while True:
    x_axis = float(input("x axis: -1,1"))
    y_axis = float(input("y axis: -1, 1"))
    power_left, power_right = mix(yaw=x_axis, throttle=y_axis)
    set_speed(power_left, power_right)