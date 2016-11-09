Include "UI.bb"

Const HUD_COL_LIGHT_R=192
Const HUD_COL_LIGHT_G=224
Const HUD_COL_LIGHT_B=255

Const HUD_COL_DARK_R=32
Const HUD_COL_DARK_G=48
Const HUD_COL_DARK_B=96

Const HUD_LIVES_ICON_RADIUS=2

Const HUD_SCORE_CHARACTERS=8
Const HUD_SCORE_NAMECHARACTERS=3

Const HUD_SCORENUM_FRAMES=10
Const HUD_SCORELETTER_FRAMES=32

Const HUD_SCORE_CHARACTERWIDTH=64
Const HUD_SCORE_CHARACTERHEIGHT=64

Const HUD_SCORE_XRATIO#=16
Const HUD_SCORE_YRATIO#=12

Const HUD_ASC_START_LETTER=64
Const HUD_ASC_START_NUM=48

Global HUD_LIVES_ICON[PLAYER_LIVESMAX-1]
Global HUD_NUMBERS
Global HUD_LETTERS
Global HUD_SCOREBOARD
Global HUD_HISCOREBOARD
Global HUD_SCOREBOARDX
Global HUD_SCOREBOARDY


Function CreateNumbersHUDIconImage()
	Local W=HUD_SCORE_CHARACTERWIDTH
	Local H=HUD_SCORE_CHARACTERHEIGHT
	HUD_NUMBERS=CreateImage(W,H,HUD_SCORENUM_FRAMES)
	
	Local X
	Local Y
	Local Frame
	Local Buffer
	Local Byte
	Local Colour
	
	Restore BMN_
	
	For Frame=0 To HUD_SCORENUM_FRAMES-1
		Buffer=ImageBuffer(HUD_NUMBERS,Frame)
		LockBuffer Buffer
		For Y=0 To H-1
			For X=0 To W-1
				Read Byte
				Colour=Byte + (Byte Shl 8)+(Colour Shl 16)
				WritePixelFast X,Y,Colour,Buffer
			Next
		Next
		UnlockBuffer Buffer
	Next
End Function

Function CreateLettersHUDIconImage()
	Local W=HUD_SCORE_CHARACTERWIDTH
	Local H=HUD_SCORE_CHARACTERHEIGHT
	HUD_LETTERS=CreateImage(W,H,HUD_SCORELETTER_FRAMES)
	
	Local X
	Local Y
	Local Frame
	Local Buffer
	Local Byte
	Local Colour
	
	Restore BMF_
	
	For Frame=0 To HUD_SCORELETTER_FRAMES-1
		Buffer=ImageBuffer(HUD_LETTERS,Frame)
		LockBuffer Buffer
		For Y=0 To H-1
			For X=0 To W-1
				Read Byte
				Colour=Byte + (Byte Shl 8)+(Colour Shl 16)
				WritePixelFast X,Y,Colour,Buffer
			Next
		Next
		UnlockBuffer Buffer
	Next
	
End Function

Function CreateLivesLeftHUDIcons()
	Local Life
	For Life=0 To PLAYER_LIVESMAX-1
		HUD_LIVES_ICON[Life]=CreateSphere(POLYCOUNT_DETAIL)
		ScaleEntity HUD_LIVES_ICON[Life],HUD_LIVES_ICON_RADIUS,HUD_LIVES_ICON_RADIUS,HUD_LIVES_ICON_RADIUS
		CreateShadow(HUD_LIVES_ICON_RADIUS Shr True,HUD_LIVES_ICON_RADIUS Shr True,HUD_LIVES_ICON[Life])
		PositionEntity HUD_LIVES_ICON[Life],BOUNDARY_W-((Life+1)*HUD_LIVES_ICON_RADIUS Shl 2),0-(HUD_LIVES_ICON_RADIUS Shl True),GENERIC_Z_OFFSET,True
		HideEntity HUD_LIVES_ICON[Life]
	Next
End Function

Function HideRemainingLives()
	Local Life
		For Life=0 To PLAYER_LIVESMAX-1
			HideEntity HUD_LIVES_ICON[Life]
		Next
End Function

Function ShowRemainingLives()
	HideRemainingLives
	
	If (STATE=STATE_GAME)
		;Because there were Rare occasions where CPU can lose a life...
		If (PLAYER_LIVES>1)
			Local Life
			For Life=1 To PLAYER_LIVES-1
				ShowEntity HUD_LIVES_ICON[Life-1]
			Next
		End If
		
	End If
		
End Function	

Function ShowHud()
	Select (STATE)
		Case STATE_GAME:
			ShowScore
		Case STATE_ATTRACT:
			ShowHiScore
			ShowUI
		Case STATE_HISCORE_ENTRY:
			ShowEntryInits
			ShowScore
			ShowUI
	End Select
End Function

Function ShowEntryInits()
	If (HUD_ENTRY_INITS_IMAGE) 
		DrawImage HUD_ENTRY_INITS_IMAGE,(GWIDTH*0.5)-(HUD_SCORE_CHARACTERWIDTH+ImageWidth(HUD_ENTRY_INITS_IMAGE)),HUD_SCOREBOARDY
	End If
End Function
	
Function ShowScore()
	DrawImage HUD_SCOREBOARD,HUD_SCOREBOARDX,HUD_SCOREBOARDY
End Function

Function ShowHiScore()
	DrawImage HUD_HISCOREBOARD,HUD_SCOREBOARDX,HUD_SCOREBOARDY
End Function

Function CreateScoreBoard()
	HUD_SCOREBOARD=CreateImage(HUD_SCORE_CHARACTERS*HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERHEIGHT)
	MaskImage HUD_SCOREBOARD,0,0,0
	CameraProject CAMERA,0,0,0
	HUD_SCOREBOARDX=HUD_SCORE_CHARACTERWIDTH;GWIDTH-ImageWidth(HUD_SCOREBOARD)
	HUD_SCOREBOARDY=ProjectedY();0+(HUD_SCORE_CHARACTERHEIGHT Shr True);
End Function

Function CreateHiScoreBoard()
	HUD_HISCOREBOARD=CreateImage((HUD_SCORE_CHARACTERS+HUD_SCORE_NAMECHARACTERS+1)*HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERHEIGHT)
	MaskImage HUD_HISCOREBOARD,0,0,0
	UpdateHiScoreBoard
End Function

Function UpdateScoreBoard()
	Local Buffer=GraphicsBuffer()
	SetBuffer ImageBuffer(HUD_SCOREBOARD)
;	LockBuffer GraphicsBuffer()
	Local X
	Local Y
	
	ClsColor 0,0,0
	Cls
	
	Local ScoreString$=Right(String("0",HUD_SCORE_CHARACTERS)+Str(SCORE),HUD_SCORE_CHARACTERS)
	
	Local Iter
	Local Char$
	Local Frame
	
	For Iter=1 To Len(ScoreString)
		X=(Iter-1)*HUD_SCORE_CHARACTERWIDTH
		Char=Mid(ScoreString,Iter,1)
		Frame=Asc(Char)-48
		CopyRect 0,0,HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERWIDTH,X,0,ImageBuffer(HUD_NUMBERS,Frame)
		;DrawImage HUD_NUMBERS,X,0,Frame
	Next
	
	SetBuffer Buffer
End Function

Function UpdateHiScoreBoard()
	Local Buffer=GraphicsBuffer()
	SetBuffer ImageBuffer(HUD_HISCOREBOARD)
;	LockBuffer GraphicsBuffer()
	Local X
	Local Y
	
	ClsColor 0,0,0
	Cls
	
	Local ScoreString$=Right(String("0",HUD_SCORE_CHARACTERS)+Str(HISCORE),HUD_SCORE_CHARACTERS)
	
	Local Iter
	Local Char$
	Local Frame
	
	For Iter=1 To Len(HISCOREINITS)
		X=(Iter-1)*HUD_SCORE_CHARACTERWIDTH
		Char=Mid(HISCOREINITS,Iter,1)
		Frame=GetLetterFrame(Char)
		CopyRect 0,0,HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERWIDTH,X,0,ImageBuffer(HUD_LETTERS,Frame)
	Next
	
	For Iter=1 To Len(ScoreString)
		X=((Iter-1) + HUD_SCORE_NAMECHARACTERS+1) * HUD_SCORE_CHARACTERWIDTH
		Char=Mid(ScoreString,Iter,1)
		Frame=GetNumberFrame(Char)
		CopyRect 0,0,HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERWIDTH,X,0,ImageBuffer(HUD_NUMBERS,Frame)
		;DrawImage HUD_NUMBERS,X,0,Frame
	Next
	
	SetBuffer Buffer
End Function

Function GetNumberFrame(Char$)
	Return (Asc(Char)-HUD_ASC_START_NUM)
End Function
	
Function GetLetterFrame(Char$)
	Return (Asc(Char)-HUD_ASC_START_LETTER)
End Function
;~IDEal Editor Parameters:
;~F#24#40#5D#68#6F#7F#8D#93#97#9B#A3#A9#C4#E6#EA
;~C#Blitz3D