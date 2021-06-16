В области настройки игры (доступно Сенсею ивента) существуют ряд опций, которые отвечают за раунды.

### boolean \[Rounds] Enabled
Включает для игры режим раундов. Если режим выключен, то все участники подключившиеся к ивенту будут играть на одном игровом поле. Если режим включен, то каждый участник будет попадать на отдельное игровое поле с дргуими участниками в строго ограниченном количестве, проводить там серию Раундов (составляющих Матч) и затем после победы или поражения попадать на новое игровое поле к другим участникам с тем, чтобы провести новый Матч, состоящий из серии Раундов.

### integer \[Rounds] Players per room
Указывает на количество участников в игровом поле. Матч не начнется, пока не соберется заданное количество участников. Все участники до начала Матча будут неактивны и изображены символами погибших героев, эти места указывают на точку спауна.

### integer \[Rounds] Time per Round
Количество тиков, которое будет длиться 1 Раунд.

Напомню, что Матч состоит из серии Раундов, где игроки играют в 1 составе. Игрок проигравший в Раундах (всех кроме последнего) становятся неактивными (изображены символами погибших героев) и ждут начала следующего Раунда. Из последнего Раунда проигравший игрок немедленно попадает на новое игровое поле с новыми участниками и так происходит естественное "перемешивание" игроков между Матчами.

Игроки "перемешиваются" **всегда** после окончания (победой или поражением) последнего Раунда Матча. Если в Матче 1 Раунд, это "перемешивание" соответственно делается сразу. Если в Матче более 1 Раунда, то перемешивание будет после окончания **всех** Раундов. Под "перемешиванием" стоит понимать накопление нового игрового поля участниками выбывающими из разных игровых комнат в произвольное время.

В случае, если на игровом поле по прошествию таймаута остается несколько игроков, то победитель определяется по внутренним правилам игры. Это либо общее количество очков заработанных игроком за время этого Раунда, либо накопительные свойства героя (например, длинна змейки на поле).

Математика расчета этого значения простая. Допустим планируется ивент на 2 часа, это 2*60*60 = 7200 тиков. За это время хочется провести 10 Матчей по 1 Раунду каждый. Тогда 7200 / 10 / 1 = 720 тиков на Раунд. Если при этом поставить 3 раунда на Матч, тогда 1 Рануд  продлится 7200 / 10 / 3  = 240 тиков.

### integer \[Rounds] Time for Winner
Количество тиков для победителя Раунда, которое он проведет на этом (уже пустом) игровом поле, дабы собрать всевозможные "плюшки" и заработать дополнительно очков. Опция имеет смысл только, если дает возможность заработать очки, иначе в ней стоит оставить 1 тик.

### integer \[Rounds] Time before start Round
Обратный счетчик перед началом каждого Раунда Матча. ...5..., ...4..., ..3.., ..2.., .1., Round 1 Fight!

### integer \[Rounds] Rounds per Match
Количество Раундов на 1 Матч. Во время всех этих раундов состав игроков на игровом поле будет неизменный. Если указать 1, то Раунд = Матчу, что придаст игре оттенок динамичности - проигравший в таком Раунде/Матче сразу же покидает игровое поле  и попадает на новое игровое поле к другим участникам, проигравшим в других Ранудах/Матчах.

### integer \[Rounds] Min ticks for win
Минимально необходимое количество тиков прошедших с начала Раунда, чтобы засчитать победителю его законную победу. Опция служит для тех случаев, когда в начале турнира участники еще не написали своих ботов и их персонажы проигрывают на первых тиках Раунда, чтобы не давать преимуществ тому, кто уже начал что-то предпринимать на поле боя.

Стоит помнить, что при изменении этих настроек после их сохранения стоит кликнуть на *Reload all players* дабы остановить все текущие Раунды/Матчи и переукомплектовать игровые поля. 