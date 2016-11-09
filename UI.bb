Include"HiScoreEntry.bb"
Include"Credits.bb"

Global UI_CURRENT_MENUOPTION.OPTIONS
Global UI_CURRENT_MENU_IMAGE

Const UI_NAME_PLAY$="PLAY"
Const UI_NAME_SETTINGS$="SETTINGS"
Const UI_NAME_CONTROLS$="CONTROLS"

Const UI_NAME_KEY$="KEYBOARD"
Const UI_NAME_JOY$="PADSTICK"
Const UI_NAME_SETBACK$="BACK"

Type OPTIONS
	Field Ref
	Field Name$
	Field Exchange.OPTIONS
End Type

Function DefineMenuOptions()
	Local oPlay.OPTIONS=New OPTIONS
	Local oSettings.OPTIONS=New OPTIONS
	Local oControls.OPTIONS=New OPTIONS
	Local oJoy.OPTIONS=New OPTIONS
	Local oKey.OPTIONS=New OPTIONS
	
	Local oBack.OPTIONS=New OPTIONS
	
	oPlay\Name=UI_NAME_PLAY
	oPlay\Exchange=oSettings
	
	oSettings\Name=UI_NAME_SETTINGS
	oSettings\Exchange=oPlay
	
	oControls\Name=UI_NAME_CONTROLS
	oControls\Exchange=oBack
	
	oBack\Name=UI_NAME_SETBACK
	oBack\Exchange=oControls
	
	oJoy\Name=UI_NAME_JOY
	oJoy\Exchange=oKey
	
	oKey\Name=UI_NAME_KEY
	oKey\Exchange=oJoy
End Function

Function UI_DisplayTextImage(Content$)
	Local Image=CreateImage(Len(Content)*HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERWIDTH)
	Local Buffer=GraphicsBuffer()
	SetBuffer ImageBuffer(Image)
	;	LockBuffer GraphicsBuffer()
	Local X
	Local Y
	
	ClsColor 0,0,0
	Cls
	
	Local Iter
	Local Char$
	Local Frame
	
	For Iter=1 To Len(Content)
		X=(Iter-1)*HUD_SCORE_CHARACTERWIDTH
		Char=Mid(Content,Iter,1)
		Frame=GetLetterFrame(Char)
		CopyRect 0,0,HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERWIDTH,X,0,ImageBuffer(HUD_LETTERS,Frame)
	Next
	
	SetBuffer Buffer
	
	Return Image
End Function	

Function ShowUI()
	If (UI_CURRENT_MENU_IMAGE)
		DrawImage UI_CURRENT_MENU_IMAGE,GWIDTH*0.5,GHEIGHT*0.5
	Else
		UI_CURRENT_MENU_IMAGE=UI_DisplayTextImage(UI_CURRENT_MENUOPTION\Name)
		MidHandle UI_CURRENT_MENU_IMAGE
		DrawImage UI_CURRENT_MENU_IMAGE,GWIDTH*0.5,GHEIGHT*0.5
	End If
End Function

Function MenuControl()
	Local ConfigurationChange=False
	If ((KeyHit(CTRL_KEY_LAUNCH) Or JoyHit(CTRL_JOY_LAUNCH,JOY_PORT)))
		
		WriteLog(UI_CURRENT_MENUOPTION\Name+" Selection made from from UI")
		Select UI_CURRENT_MENUOPTION\Name
			Case UI_NAME_PLAY:
				FreeImage UI_CURRENT_MENU_IMAGE
				UI_CURRENT_MENU_IMAGE=0
				PlayGameEvent
			Case UI_NAME_SETTINGS:
				UI_CURRENT_MENUOPTION=SelectMenuOptionByName(UI_NAME_CONTROLS)
			Case UI_NAME_SETBACK:
				UI_CURRENT_MENUOPTION=SelectMenuOptionByName(UI_NAME_PLAY)
			Case UI_NAME_CONTROLS:
				UI_CURRENT_MENUOPTION=SelectMenuOptionByCurrentControlMethod()	
			Case UI_NAME_JOY:
				
				If (CURRENT_CONTROL_METHOD<>CONTROL_METHOD_JOYSTICK)
					WriteLog("Control method changed to joystick/pad controller.")
					CURRENT_CONTROL_METHOD=CONTROL_METHOD_JOYSTICK
					ConfigurationChange=True
				Else
					WriteLog("Current control method Joystick was already selected. No changes made")
				End If
				
				UI_CURRENT_MENUOPTION=SelectMenuOptionByName(UI_NAME_PLAY)
				
			Case UI_NAME_KEY:
				
				If (CURRENT_CONTROL_METHOD<>CONTROL_METHOD_KEYBOARD)
					WriteLog("Control method changed to keyboard.")
					CURRENT_CONTROL_METHOD=CONTROL_METHOD_KEYBOARD
					ConfigurationChange=True
				Else
					WriteLog("Current control method keyboard was already selected. No changes made")
				End If
				
				UI_CURRENT_MENUOPTION=SelectMenuOptionByName(UI_NAME_PLAY)
				
		End Select
	End If
	
	If (ConfigurationChange)
		WriteLog("Configuration settings have been changed. Attempting to write cahnges to disk.")
		WriteConfig
	End If
	
	Local Change=ControlUIInputResult()
	If (Change)
		Select (Change)
			Case -1:
				UI_CURRENT_MENUOPTION=UI_CURRENT_MENUOPTION\Exchange
			Case 1:
				UI_CURRENT_MENUOPTION=UI_CURRENT_MENUOPTION\Exchange
		End Select
		
		FreeImage UI_CURRENT_MENU_IMAGE
		UI_CURRENT_MENU_IMAGE=0
		
	End If
End Function

Function SelectMenuOptionByCurrentControlMethod.OPTIONS()
	Local Name$
	If (CURRENT_CONTROL_METHOD=CONTROL_METHOD_JOYSTICK) Then Name=UI_NAME_JOY
	If (CURRENT_CONTROL_METHOD=CONTROL_METHOD_KEYBOARD) Then Name=UI_NAME_KEY
	
	Return SelectMenuOptionByName(Name)
End Function

Function SelectMenuOptionByName.OPTIONS(Name$)
	If UI_CURRENT_MENU_IMAGE
		FreeImage UI_CURRENT_MENU_IMAGE
		UI_CURRENT_MENU_IMAGE=0
	End If
	
	Local o.OPTIONS
	For o=Each OPTIONS
		If (o\Name=Name)
			Return o
		End If
	Next
	Return Null
End Function

Function PlayGameEvent()
	WriteLog("Play Game event")
	ChangeToGameMode
	ClearRound
	StartGame
End Function

Function CleanUI()
	If UI_CURRENT_MENU_IMAGE
		FreeImage UI_CURRENT_MENU_IMAGE
		UI_CURRENT_MENU_IMAGE=0
	End If
	
	UI_CURRENT_MENUOPTION=First OPTIONS
End Function

;~IDEal Editor Parameters:
;~F#E#14#30#4B#55#94#9C#AB#B2
;~C#Blitz3D