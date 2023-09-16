import keyboard
from enum import Enum

class KeyboardStates(Enum):
    NORMAL = 1
    NUMERIC = 2
    SELECTION = 3

keyboard_state = KeyboardStates.NORMAL

def change_keyboard_state():
    global keyboard_state
    keyboard.unhook_all()
    keyboard.add_hotkey("alt gr", change_keyboard_state)
    if keyboard_state == KeyboardStates.NORMAL:
        # print("keyboard state is set to NUMERIC")
        # keyboard_state = KeyboardStates.NUMERIC
        # set_numeric_state()
        print("keyboard state is set to SELECTION")
        keyboard_state = KeyboardStates.SELECTION
        set_selection_state()
    # elif keyboard_state == KeyboardStates.NUMERIC:
    #     print("keyboard state is set to SELECTION")
    #     keyboard_state = KeyboardStates.SELECTION
    #     set_selection_state()
    elif keyboard_state == KeyboardStates.SELECTION:
        print("keyboard state is set to NORMAL")
        keyboard_state = KeyboardStates.NORMAL

def set_numeric_state():
    keyboard.remap_key("space", "shift+à") # 0
    keyboard.remap_key("j", "shift+'") # 4
    keyboard.remap_key("k", "shift+(") # 5
    keyboard.remap_key("l", "shift+§") # 6
    keyboard.remap_key("u", "shift+è") # 7
    keyboard.remap_key("i", "shift+!") # 8
    keyboard.remap_key("o", "shift+ç") # 9
    keyboard.remap_key(",", "shift+&") # 1
    keyboard.remap_key(";", "shift+é") # 2
    keyboard.remap_key(":", "shift+\"") # 3

def set_selection_state():
    keyboard.remap_key("!", 71) # Home
    keyboard.remap_key("i", 79) # End
    keyboard.remap_key("u", 83) # Delete
    keyboard.remap_key("ç", 73) # Page Up
    keyboard.remap_key("o", 81) # Dage Down
    keyboard.remap_key(";", 80) # down
    keyboard.remap_key("k", 72) # up
    keyboard.remap_key("j", 75) # left
    keyboard.remap_key("l", 77) # right
# keyboard.remap_hotkey("shift+!", 'shift+7') # shift+home
# keyboard.remap_hotkey("shift+i", 'shift+1') # shift+end
# keyboard.remap_hotkey("shift+j", 'shift+4') # shift+left
# keyboard.remap_hotkey("shift+l", 'shift+6') # shift+right
# keyboard.remap_hotkey("shift+k", 'shift+8') # shift+up
# keyboard.remap_hotkey("shift+;", 'shift+2') # shift+down

keyboard.add_hotkey("alt gr", change_keyboard_state)

# while True:
#     # Wait for the next event.
#     event = keyboard.read_event()
#     print(event)

keyboard.wait()