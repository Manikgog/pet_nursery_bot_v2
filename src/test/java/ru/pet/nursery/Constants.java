package ru.pet.nursery;

import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.entity.Nursery;
import ru.pet.nursery.entity.User;
import ru.pet.nursery.enumerations.AnimalType;
import ru.pet.nursery.enumerations.Gender;
import ru.pet.nursery.web.dto.AnimalDTO;

import java.time.LocalDate;

public class Constants {

    public static User USER;
    public static User GARRY_POTTER;
    public static User GERMIONA_GREINDGER;
    public static User RON_WISLY;
    public static User POLUMNA_LAVGOOD;
    static {
        USER.setTelegramUserId(1);
        USER.setFirstName("нет");
        USER.setLastName("нет");
        USER.setUserName("нет");
        USER.setAddress("нет");
        USER.setPhoneNumber("нет");

        GARRY_POTTER.setTelegramUserId(2);
        GARRY_POTTER.setFirstName("Гарри");
        GARRY_POTTER.setLastName("Поттер");
        GARRY_POTTER.setUserName("G.Potter");
        GARRY_POTTER.setAddress("Астана ул. Малышева д.123 кв.321");
        GARRY_POTTER.setPhoneNumber("8-987-654-321-98");

        GERMIONA_GREINDGER.setTelegramUserId(3);
        GERMIONA_GREINDGER.setFirstName("Гермиона");
        GERMIONA_GREINDGER.setLastName("Грейнджер");
        GERMIONA_GREINDGER.setUserName("G.Greyndger");
        GERMIONA_GREINDGER.setAddress("Астана ул. 8 Марта д.12 кв.21");
        GERMIONA_GREINDGER.setPhoneNumber("8-123-654-987-12");

        RON_WISLY.setTelegramUserId(4);
        RON_WISLY.setFirstName("Рон");
        RON_WISLY.setLastName("Уизли");
        RON_WISLY.setUserName("R.Wisly");
        RON_WISLY.setAddress("Астана ул. Куйбышева д.54 кв.87");
        RON_WISLY.setPhoneNumber("8-987-896-547-32");

        POLUMNA_LAVGOOD.setTelegramUserId(5);
        POLUMNA_LAVGOOD.setFirstName("Полумна");
        POLUMNA_LAVGOOD.setLastName("Лавгуд");
        POLUMNA_LAVGOOD.setUserName("P.Lavgood");
        POLUMNA_LAVGOOD.setAddress("Астана ул. Космонавтов д.65 кв.25");
        POLUMNA_LAVGOOD.setPhoneNumber("8-987-325-798-62");
    }


    public static Nursery NURSERY_1;
    public static Nursery NURSERY_2;
    static{
        NURSERY_1.setId(1);
        NURSERY_1.setAddress("Астана Калинина д.24");
        NURSERY_1.setPhoneNumber("8-965-569-326-54");

        NURSERY_2.setId(2);
        NURSERY_2.setAddress("Астана ул. Кировградская д.32");
        NURSERY_2.setPhoneNumber("8-954-568-98-97");
    }

    public final static AnimalDTO VASKA_DTO;
    public final static AnimalDTO PALKAN_DTO;

    public final static Animal VASKA;
    public final static Animal PALKAN;
    static {
        VASKA_DTO = new AnimalDTO();
        VASKA_DTO.setAnimalName("Васька");
        VASKA_DTO.setAnimalType(AnimalType.CAT);
        VASKA_DTO.setGender(Gender.MALE);
        VASKA_DTO.setDescription("Рыжий кот. Левое ухо ободрано.");
        VASKA_DTO.setBirthDate(LocalDate.of(2023,1,1));
        VASKA_DTO.setNurseryId(1);

        PALKAN_DTO = new AnimalDTO();
        PALKAN_DTO.setAnimalName("Палкан");
        PALKAN_DTO.setAnimalType(AnimalType.DOG);
        PALKAN_DTO.setGender(Gender.MALE);
        PALKAN_DTO.setDescription("Немецкая овчарка.");
        PALKAN_DTO.setBirthDate(LocalDate.of(2020,1,1));
        PALKAN_DTO.setNurseryId(2);

        VASKA = new Animal();
        VASKA.setId(1);
        VASKA.setAnimalName("Васька");
        VASKA.setAnimalType(AnimalType.CAT);
        VASKA.setGender(Gender.MALE);
        VASKA.setBirthDate(LocalDate.of(2023,1,1));
        VASKA.setUser(USER);
        VASKA.setNursery(NURSERY_1);
        VASKA.setDescription("Рыжий кот. Левое ухо ободрано.");

        PALKAN = new Animal();
        PALKAN.setId(2);
        PALKAN.setAnimalName("Палкан");
        PALKAN.setAnimalType(AnimalType.DOG);
        PALKAN.setGender(Gender.MALE);
        PALKAN.setBirthDate(LocalDate.of(2020,1,1));
        PALKAN.setUser(USER);
        PALKAN.setNursery(NURSERY_2);
        PALKAN.setDescription("Немецкая овчарка.");

    }


    public static String volunteerCommand = "{\n" +
            "  \"update_id\":436028079,\n" +
            "  \"message\": \n" +
            "    {\n" +
            "      \"message_id\":164,\n" +
            "      \"from\":\n" +
            "        {\n" +
            "          \"id\":1874598997,\n" +
            "          \"is_bot\":false,\n" +
            "          \"first_name\":\"Максим\",\n" +
            "          \"last_name\":null,\n" +
            "          \"username\":\"Manikgog\",\n" +
            "          \"language_code\":\"en\",\n" +
            "          \"can_join_groups\":null,\n" +
            "          \"can_read_all_group_messages\":null,\n" +
            "          \"supports_inline_queries\":null\n" +
            "        },\n" +
            "      \"sender_chat\":null,\n" +
            "      \"date\":1714882234,\n" +
            "      \"chat\":\n" +
            "        {\n" +
            "          \"id\":1874598997,\n" +
            "          \"type\":\"Private\",\n" +
            "          \"first_name\":\"Максим Гоголин\",\n" +
            "          \"last_name\":null,\n" +
            "          \"username\":\"Manikgog\",\n" +
            "          \"title\":null,\n" +
            "          \"photo\":null,\n" +
            "          \"bio\":null,\n" +
            "          \"has_private_forwards\":null,\n" +
            "          \"description\":null,\n" +
            "          \"invite_link\":null,\n" +
            "          \"pinned_message\":null,\n" +
            "          \"permissions\":null,\n" +
            "          \"slow_mode_delay\":null,\n" +
            "          \"message_auto_delete_time\":null,\n" +
            "          \"has_protected_content\":null,\n" +
            "          \"sticker_set_name\":null,\n" +
            "          \"can_set_sticker_set\":null,\n" +
            "          \"linked_chat_id\":null,\n" +
            "          \"location\":null\n" +
            "        },\n" +
            "      \"forward_from\":null,\n" +
            "      \"forward_from_chat\":null,\n" +
            "      \"forward_from_message_id\":null,\n" +
            "      \"forward_signature\":null,\n" +
            "      \"forward_sender_name\":null,\n" +
            "      \"forward_date\":null,\n" +
            "      \"is_automatic_forward\":null,\n" +
            "      \"reply_to_message\":null,\n" +
            "      \"via_bot\":null,\n" +
            "      \"edit_date\":null,\n" +
            "      \"has_protected_content\":null,\n" +
            "      \"media_group_id\":null,\n" +
            "      \"author_signature\":null,\n" +
            "      \"text\":\"/volunteer\",\n" +
            "      \"entities\":[\n" +
            "                  {\n" +
            "                    \"type\":3,\n" +
            "                    \"offset\":0,\n" +
            "                    \"length\":10,\n" +
            "                    \"url\":null,\n" +
            "                    \"user\":null,\n" +
            "                    \"language\":null\n" +
            "                  }\n" +
            "              ],\n" +
            "      \"caption_entities\":null,\n" +
            "      \"audio\":null,\n" +
            "      \"document\":null,\n" +
            "      \"animation\":null,\n" +
            "      \"game\":null,\n" +
            "      \"photo\":null,\n" +
            "      \"sticker\":null,\n" +
            "      \"video\":null,\n" +
            "      \"voice\":null,\n" +
            "      \"video_note\":null,\n" +
            "      \"caption\":null,\n" +
            "      \"contact\":null,\n" +
            "      \"location\":null,\n" +
            "      \"venue\":null,\n" +
            "      \"poll\":null,\n" +
            "      \"dice\":null,\n" +
            "      \"new_chat_members\":null,\n" +
            "      \"left_chat_member\":null,\n" +
            "      \"new_chat_title\":null,\n" +
            "      \"new_chat_photo\":null,\n" +
            "      \"delete_chat_photo\":null,\n" +
            "      \"group_chat_created\":null,\n" +
            "      \"supergroup_chat_created\":null,\n" +
            "      \"channel_chat_created\":null,\n" +
            "      \"message_auto_delete_timer_changed\":null,\n" +
            "      \"migrate_to_chat_id\":null,\n" +
            "      \"migrate_from_chat_id\":null,\n" +
            "      \"pinned_message\":null,\n" +
            "      \"invoice\":null,\n" +
            "      \"successful_payment\":null,\n" +
            "      \"connected_website\":null,\n" +
            "      \"passport_data\":null,\n" +
            "      \"proximity_alert_triggered\":null,\n" +
            "      \"voice_chat_started\":null,\n" +
            "      \"voice_chat_ended\":null,\n" +
            "      \"voice_chat_participants_invited\":null,\n" +
            "      \"voice_chat_scheduled\":null,\n" +
            "      \"reply_markup\":null\n" +
            "    },\n" +
            "  \"edited_message\":null,\n" +
            "  \"channel_post\":null,\n" +
            "  \"edited_channel_post\":null,\n" +
            "  \"inline_query\":null,\n" +
            "  \"chosen_inline_result\":null,\n" +
            "  \"callback_query\":null,\n" +
            "  \"shipping_query\":null,\n" +
            "  \"pre_checkout_query\":null,\n" +
            "  \"poll\":null,\n" +
            "  \"poll_answer\":null,\n" +
            "  \"my_chat_member\":null,\n" +
            "  \"chat_member\":null,\n" +
            "  \"chat_join_request\":null\n" +
            "}";
}
