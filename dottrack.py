import cv2
import numpy as np

cap = cv2.VideoCapture(0)

while True:
    # Capture frame-by-frame
    ret, frame = cap.read()

    # Get the dimensions of the frame
    height, width = frame.shape[:2]

    # Calculate the center of the frame
    center_x, center_y = width // 2, height // 2

    # Draw the x-axis (horizontal) and y-axis (vertical) through the center
    cv2.line(frame, (0, center_y), (width, center_y), (8, 81, 100), 3)  # x-axis
    cv2.line(frame, (center_x, 0), (center_x, height), (8, 81, 100), 3)  # y-axis

    # Draw the number on the axis
    text_position_xright = (width - 40, height // 2)
    text_position_xleft = (0, height // 2)
    text_position_ybottom = (width // 2, height - 20)
    text_position_ytop = (width // 2, 20)
    cv2.putText(frame, str(width//2), text_position_xright, cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
    cv2.putText(frame, str(f"-{width//2}"), text_position_xleft, cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
    cv2.putText(frame, str(height//2), text_position_ytop, cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
    cv2.putText(frame, str(f"-{height//2}"), text_position_ybottom, cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)

    # Color Detection
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    lower_red = np.array([0, 125, 125])
    upper_red = np.array([10, 255, 255])
    mask = cv2.inRange(hsv, lower_red, upper_red)
    result = cv2.bitwise_and(frame, frame, mask=mask)

    # Find contours in the mask
    contours,_ = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    if contours:
        # Find the largest contour
        largest_contour = max(contours, key=cv2.contourArea)

        # Get the moments of the largest contour
        M = cv2.moments(largest_contour)
        if M["m00"] != 0:
            # Calculate the centre point of the contour with the highest level of gradient
            Contour_X = int(M["m10"] / M["m00"]) #Formula to calculate spactial contour (sigma)
            Contour_Y = int(M["m01"] / M["m00"]) #BTW calvin the M["m10"] and M["mOO"] stuff that looks for the coordinates of the contoid and the area of the controu the formula calculates the centre (the highest gradient level of the colour)

            # Calculate the coordinates relative to the center of the frame
            relative_x =    Contour_X - center_x
            relative_y = center_y - Contour_Y

            # Draw the centroid on the frame
            cv2.circle(result, (Contour_X, Contour_Y), 10, (255, 0, 0), -1)
            cv2.putText(result, f"({relative_x}, {relative_y})", (Contour_X + 10, Contour_Y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0), 2)

            # Print the coordinates to the console
            print(f"Red Dot Coordinates: ({relative_x}, {relative_y})")

    # Display the resulting frame with the coordinate system and detected red color
    cv2.imshow('Coordinate System', result)

    # Break the loop if the 'q' key is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything is done, release the capture and close the window
cap.release()
cv2.destroyAllWindows()
