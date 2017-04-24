Const CREDITS_DISPLAY_DURATION=2000

Type CREDITS
	Field AccreditationLine$
End Type

Function PopulateCredits()
	Restore C_
	
	Local Credit$
	Local C.CREDITS
	Credit=ReadCredit()
	
	Repeat
		C=New CREDITS
		C\AccreditationLine=Credit
		Credit=ReadCredit()
	Until (Credit="")
End Function

Function ScrollCredits()
	FlushKeys
	FlushJoy
	
	Local MaintainCredits=True
	Local X=GWIDTH*0.5
	Local Y=GHEIGHT*0.5
	Local Image
	Local C.CREDITS=First CREDITS
	
	Local TimeDifference
	Local CreditsDisplayTimestamp=MilliSecs()
	
	Image=UI_DisplayTextImage(C\AccreditationLine)
	HandleImage Image,ImageWidth(Image)*0.5,0
	
	While MaintainCredits
		RenderWorld
		
		TimeDifference= MilliSecs()-CreditsDisplayTimestamp
		If  (TimeDifference>CREDITS_DISPLAY_DURATION)
			C=After C
			
			If (Image) 
				FreeImage Image
			End If
			
			If (C=Null) 
				MaintainCredits=False
				Exit
			End If
			
			Image=UI_DisplayTextImage(C\AccreditationLine)
			MidHandle Image
			
			CreditsDisplayTimestamp=MilliSecs()-(TimeDifference-CREDITS_DISPLAY_DURATION)
		End If
		
		DrawImage Image,X,Y
		
		Flip True
		
		If( (KeyHit(CTRL_KEY_LAUNCH))Or(JoyHit(CTRL_JOY_LAUNCH,JOY_PORT)))
			MaintainCredits=False
			Exit
		End If
		
	Wend
	
	If (Image)
		FreeImage Image
	End If
	
	If (STATE<>STATE_ATTRACT)
		ChangeToAttractMode
	End If
End Function

Function ReadCredit$()
	Local C$
	Local Byte
	Read Byte
	
	If (Byte=-1)
		Return ""
	Else
		While (Byte)
			If (Byte=0) Then Exit
			If (Byte<27) Then C=C+Chr(Byte+64)
			Read Byte
		Wend
	End If

	C= Replace(Trim(C)," ","")
	
	Return C
End Function
;~IDEal Editor Parameters:
;~F#2#6#14#4E
;~C#Blitz3D