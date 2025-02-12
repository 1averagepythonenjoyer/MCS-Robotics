import math

selfpos = [None]*3

def rotate_tags(tags):
    rotation = (R.zone + 2) % mod 4
    for i in range(rotations):
        for tag in tags:
            x = tag[0]
            y = tag[1]
            tag[0] = -1*y
            tag[1] = x

wall_tags = [
    [-250, 300, 0],
    [-150, 300, 0],
    [-50, 300, 0],
    [50, 300, 0],
    [150, 300, 0],
    [250, 300, 0],
    [300, 250, 90],
    [300, 150, 90],
    [300, 50, 90],
    [300, -50, 90],
    [300, -150, 90],
    [300, -250, 90],
    [250, -300, 180],
    [150, -300, 180],
    [50, -300, 180],
    [-50, -300, 180],
    [-150, -300, 180],
    [-250, -300, 180],
    [-300, -250, -90],
    [-300, -150, -90],
    [-300, -50, -90],
    [-300, 50, -90],
    [-300, 150, -90],
    [-300, 250, -90],
]

def compute_vector(dist, angle):
    if angle < 0:
        angle_to_x_axis = -90+angle
    else:
        angle_to_x_axis = 90-angle
    vector = [round(dist * math.cos(math.radians(angle_to_x_axis)), 5), round(dist * math.cos(math.radians(angle)), 5)]
    return vector

def pos_update(distance, bearing, rotation, id):
    id -= 100
    tag_normal = wall_tags[id][2]
    global selfpos
    selfpos[2] = tag_normal + rotation - bearing
    vector_to_tag = compute_vector(distance, tag_normal + bearing)
    #print(vector_to_tag)
    selfpos[0] = wall_tags[id][0] - vector_to_tag[0]
    selfpos[1] = wall_tags[id][1] - vector_to_tag[1]

#pos_update(2, 30, 60, 120)
#print(selfpos)
