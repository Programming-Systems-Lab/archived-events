
testSubscriberps.dll: dlldata.obj testSubscriber_p.obj testSubscriber_i.obj
	link /dll /out:testSubscriberps.dll /def:testSubscriberps.def /entry:DllMain dlldata.obj testSubscriber_p.obj testSubscriber_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \

.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0400 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del testSubscriberps.dll
	@del testSubscriberps.lib
	@del testSubscriberps.exp
	@del dlldata.obj
	@del testSubscriber_p.obj
	@del testSubscriber_i.obj
