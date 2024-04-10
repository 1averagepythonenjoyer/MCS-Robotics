from approxeng.input.selectbinder import ControllerResource # remember to install
while True:
  try: 
    with ControllerResource() as joystick:
      while joystick.connected:
        rvalue = joystick['ry']
        lvalue = joystick['ly']
        print("Lvalue =", lvalue)
        print("rvalue =", rvalue)
  except:
