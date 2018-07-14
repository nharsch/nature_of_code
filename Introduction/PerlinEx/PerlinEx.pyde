t = 0.0

def setup():
    size(400, 200)
    smooth()
    frameRate(10)

def draw():
    global t
    background(0)
    fill(255)

    t += 0.05

    x = noise(t)
    x = map(x,0,1,0,width)
    ellipse(x, height/2, 40, 40)

