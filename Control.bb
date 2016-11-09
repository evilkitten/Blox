Const KEY_SPACE=57
Const KEY_LEFT=203
Const KEY_RIGHT=205

Const JOY_DEFAULT_PORT=0
Const CONTROL_DEFAULT_METHOD=0
Const JOY_DEFAULT_DEADZONE#=0.35

Const CONTROL_METHOD_KEYBOARD=0
Const CONTROL_METHOD_JOYSTICK=1

Const JOY_DEFAULT_LAUNCH=1

Const JOY_RESPONSE_DURATION=250

Global CTRL_KEY_LEFT
Global CTRL_KEY_RIGHT
Global CTRL_KEY_LAUNCH

Global CTRL_JOY_LAUNCH

Global JOY_PORT
Global JOY_DEADZONE#

Global CURRENT_CONTROL_METHOD

Global JOY_TIMESTAMP

Function ControlInput()
	Select (STATE)
		Case STATE_GAME:
			MovementControl
			ControlLaunchInput
		Case STATE_ATTRACT:
			MenuControl()
			AutoControl
		Case STATE_HISCORE_ENTRY:	
			;Deal with input elsewhere.
	End Select
End Function

Function ControlLaunchInput()
	If ((ControlLaunchInputResult())*(LAUNCH=False))
		LaunchBall()
	End If
End Function

Function ControlLaunchInputResult()
	Select CURRENT_CONTROL_METHOD
		Case CONTROL_METHOD_JOYSTICK:
			Return JoyLaunchControl()
		Default:
			;CONTROL_METHOD_KEYBOARD:
			Return KeyLaunchControl()
	End Select
End Function

Function JoyLaunchControl()
	Return JoyHit(CTRL_JOY_LAUNCH,JOY_PORT)
End Function

Function KeyLaunchControl()
	Return KeyHit(CTRL_KEY_LAUNCH)
End Function

Function AutoControl()
	Local X#=EntityX(BAT,True)
	
	Local B.BALL= First BALL
	BATSPEED=EntityX(B\Entity,True)-X
	
	;Inertial dampening
	Local d1=0-(Sgn(BATSPEED))
	If (d1)
		BATSPEED=BATSPEED+Float((Float(d1)-BAT_SPEED_INCREMENT*BAT_SPEED_DAMP))
		If (0-Sgn(BATSPEED)<>d1)
			BATSPEED=0
		End If
	End If
End Function

Function MovementControl()
	
	Local x=ControlMoveInputResult()
	
	If (x)
		BATSPEED=BATSPEED+(BAT_SPEED_INCREMENT*x)
		If (Abs(BATSPEED)>=BATSPEED_MAX)
			BATSPEED=BATSPEED-(BAT_SPEED_INCREMENT*x)
		End If
	Else
		;Inertial dampening
		Local d1=0-(Sgn(BATSPEED))
		If (d1)
			BATSPEED=BATSPEED+Float((Float(d1)-BAT_SPEED_INCREMENT*BAT_SPEED_DAMP))
			If (0-Sgn(BATSPEED)<>d1)
				BATSPEED=0
			End If
		End If
	End If
End Function

Function ControlMoveInputResult()
	Select CURRENT_CONTROL_METHOD
		Case CONTROL_METHOD_JOYSTICK:
			Return JoyMoveControl()
		Default:
			;CONTROL_METHOD_KEYBOARD:
			Return KeyMoveControl()
	End Select
End Function

Function ControlUIInputResult()
	Local Key=(KeyHit(CTRL_KEY_RIGHT))-(KeyHit(CTRL_KEY_LEFT))
	Local Joy=JoyMoveControl()
	
	If (Joy)
		If ((FRAMETIMESTAMP-JOY_TIMESTAMP)<JOY_RESPONSE_DURATION)
			Joy=0
		Else
			JOY_TIMESTAMP=FRAMETIMESTAMP
		End If
	End If
	
	If ((Key=-1)Or (Joy=-1))
		Return -1
	End If
	If ((Key=1)Or (Joy=1))
		Return 1
	End If
	
	Return 0
End Function

Function KeyMoveControl()
	Return (KeyDown(CTRL_KEY_RIGHT))-(KeyDown(CTRL_KEY_LEFT))
End Function

Function JoyMoveControl()
	Local X#=JoyX(JOY_PORT)
	Local AbsX#=(Abs(X))
	If (AbsX>JOY_DEADZONE)
		Return Sgn(X)
	End If
	Return 0
End Function
;~IDEal Editor Parameters:
;~F#1C#29#2F#39#3D#41#51#66#70#86#8A
;~C#Blitz3D