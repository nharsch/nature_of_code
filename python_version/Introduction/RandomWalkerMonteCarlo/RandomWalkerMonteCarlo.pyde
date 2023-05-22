from walker import Walker


def setup():
    size(640,360)
    global w
    w = Walker()
    background(255)

def draw():
    w.step()
    w.render()

def mouseClicked():
    w.__init__()
