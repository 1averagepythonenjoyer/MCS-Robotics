from approxeng.input.selectbinder import ControllerResource

# Get a joystick
with ControllerResource() as joystick:
    # Loop until disconnected
    while joystick.connected:
        # Get a corrected tuple of values from the left stick, assign the two values to x and y
        x, y = joystick['l']
        # We can also get values as attributes:
        x, y = joystick.l
        print("x={x}")
        print("y={y}")