# BetonLangAPI

BetonLangAPI is a Bukkit plugin which manages translations for each player separately. Other plugins can define
multiple translations of the messages they display to players and BetonLangAPI will serve them an appropriate one when
asked for. It can also store players' chosen languages in the MySQL database.

BetonLangAPI doesn't come with any fancy features like replacing color codes, inserting variables, chat channels or
automatic loading of messages (though it can load _messages.yml_ file if you tell it to do that). You can register your
own method of loading translations to BetonLangAPI's map-based format and write your own wrapper function which will
insert variables and color codes into messages as you like.

This project was created because existing ones, [LanguageAPI](https://github.com/XHawk87/LanguageAPI) and
[MultiLanguage](https://github.com/anerach/MultiLanguage) were cluttered with non-configurable features and lacked one
important thing: MySQL support.

Check out the [Releases](https://github.com/Co0sh/BetonLangAPI/releases) for downloads and
[Wiki](https://github.com/Co0sh/BetonLangAPI/wiki) for documentation.