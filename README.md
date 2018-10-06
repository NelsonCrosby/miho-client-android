# Miho PC Remote - Android Client #

_Miho_ is an open source PC remote control. It implements input controls.
The goal is to make it easier to use certain PC functions when keyboard
and mouse is too inconvenient (such as when connected to a TV).

The client sends signals over the network to the PC to be controlled. This
particular client (being an Android app) provides an input device that can
be controlled via phone or tablet.

The driver (the bit that actually makes stuff happen on your PC) is located
in a different project - [`miho-driver`](https://github.com/NelsonCrosby/miho-driver).

The name is a reference to Miho Noyama, a character from _A Channel_. I won't
explain the joke, if you wanna understand, go watch it. Or not, I'm not your
mother.


> ## WARNING ##
>
> Miho is currently not only prototype-quality, but also very much insecure.
> Specifically, the protocol is raw and unencrypted, which carries risks.
> I do plan to encrypt it soon, but right now that has not yet happened.


## TODO ##

- [x] Simple mouse move and button control
- [x] Reconnect on app reload
  - ~~Right now, the app loses connection when it is unfocused and must be
    restarted to get control back.~~
- [x] Expose connection options and tweakable settings to user
- [ ] Mouse wheel/scroll support
- [ ] SSH transport layer
- [ ] Keyboard support

### Future directions ###

- App-specific controls (e.g. for media apps)
- Custom button layouts
