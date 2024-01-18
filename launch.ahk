SetKeyDelay, 70
^+j::Send {CtrlDown}c{CtrlUp}{AltDown}{Down}{AltUp}{CtrlDown}c{CtrlUp}{AltDown}{Down}{AltUp}{CtrlDown}c{CtrlUp}{AltDown}{Down}{AltUp}{CtrlDown}c{CtrlUp}
^+l::
    Send {Up}{Enter}
    Sleep 500
    activeWindow := WinActive("A")
    WinMove, ahk_id %activeWindow%, , 0, 200, 960, 680
    Send {Tab}{Tab}Alice{Tab}{Tab}{Enter}{AltDown}{Tab}{AltUp}{AltDown}{Down}{AltUp}{Up}{Enter}
    Sleep 500
    activeWindow := WinActive("A")
    WinMove, ahk_id %activeWindow%, , 960, 200,, 
    Send {Tab}{Tab}Bob{Tab}{Enter}{AltDown}{Tab}{AltUp}{AltDown}{Down}{AltUp}{Up}{Enter}
    Sleep 500
    activeWindow := WinActive("A")
    WinMove, ahk_id %activeWindow%, , 1920, 200,, 
    Send {Tab}{Tab}Charlie{Tab}{Enter}{AltDown}{Tab}{AltUp}{AltDown}{Down}{AltUp}{Up}{Enter}
    Sleep 500
    activeWindow := WinActive("A")
    WinMove, ahk_id %activeWindow%, , 2880, 200,, 
    Send {Tab}{Tab}Dave{Tab}{Enter}{AltDown}{Tab}{Tab}{Tab}{AltUp}{AltDown}{Tab}{Tab}{Tab}{Tab}{AltUp}
    return