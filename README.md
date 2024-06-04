# Описание проекта

Spring-Boot приложение, интегрированное с телеграмм-ботом.  Данные для работы будут подтягиваться из базы данных, кроме этого реализована возможность делать get-, post-, put- и delete- запросы 
для заполения и редактирования базы данных. Телеграм бот отвечает на популярные вопросы людей о том, что нужно знать и уметь, чтобы забрать животное из приюта. Также телеграм 
бот обеспечивает прием ежедневных отчетов о том, как животное приспосабливается к новой обстановке.

Для запуска приложения необходимо создать две папки для сохранения фотографий животных - animal_images и для сохранения фотографий из отчётов - report_photo в той же папке где расположен исполняемый файл программы.

java -jar nursery-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --spring.datasource.url=URL_OF_DATABASE --spring.datasource.username=DATABASE_USERNAME --spring.datasource.password=DATABASE_PASSWORD --telegram.bot.token=TOKEN

где
* URL_OF_DATABASE - url базы данных, например: jdbc:postgresql://localhost:5432/nurserydb 
* DATABASE_USERNAME, DATABASE_PASSWORD - имя пользователя / пароль к серверу базы данных
* TOKEN - токен, который берётся с помощью бота Telegram @BotFather

Если пользователь внесен в базу данных как волонтер приюта, то в боте у него появляется имя пользователя telegram, который хочет задать вопросы, и он может участвовать в диалогах, отвечая на вопросы пользователей бота.
Только волонтёры могут завершить сеанс диалога.

Также для сотрудников приюта предусмотрен API, который может управлять списком волонтеров, назначать усыновителей для домашних животных, просматривать отчеты усыновителей об их питомцах и т.д.
Для взаимодействия с API предусмотрен swagger-UI, доступный на:

http://localhost:8080/shelter/swagger-ui/index.html#/
