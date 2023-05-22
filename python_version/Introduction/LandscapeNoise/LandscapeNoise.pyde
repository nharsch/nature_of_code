scl = 60
w = 1800
h = 3600

def setup():
    global w, h, scl, cols, rows, terrain, flying, spin
    size(800, 800, P3D)
    cols = w / scl
    rows = h / scl
    yoff, flying, spin = 0,0,0

    terrain = [[y for y in range(rows)] for x in range(cols)]


def draw():
    global scl, w, h, terrain, flying, spin
    background(0)
    stroke(255)
    noFill()

    translate(width/2, height/2)
    rotateX(PI*(spin % 2));
    translate(-w/2,-h/2)

    flying -= 0.1
    spin += 0.001

    yoff = flying
    for y in range(rows):
        xoff = 0
        for x in range(cols):
            terrain[x][y] = map(noise(xoff, yoff), 0, 1, -200, 200)
            xoff += 0.5
        yoff += 0.2


    for y in range(rows-1):
        beginShape(TRIANGLE_STRIP)
        for x in range(cols):
            vertex(x*scl, y*scl, terrain[x][y])
            vertex(x*scl, (y+1)*scl, terrain[x][y+1])
            # rect(x*scl, y*scl, scl, scl)
        endShape()
