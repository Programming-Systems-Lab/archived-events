
testEventps.dll: dlldata.obj testEvent_p.obj testEvent_i.obj
	link /dll /out:testEventps.dll /def:testEventps.def /entry:DllMain dlldata.obj testEvent_p.obj testEvent_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \

.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0400 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del testEventps.dll
	@del testEventps.lib
	@del testEventps.exp
	@del dlldata.obj
	@del testEvent_p.obj
	@del testEvent_i.obj
