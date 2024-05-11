package ru.pet.nursery;

import ru.pet.nursery.entity.Animal;
import ru.pet.nursery.web.dto.AnimalDTO;

import java.time.LocalDate;

public class Constants {
    public final static AnimalDTO VASKA_DTO;
    public final static AnimalDTO PALKAN_DTO;

    public final static Animal VASKA;
    public final static Animal PALKAN;
    static {
        VASKA_DTO = new AnimalDTO();
        VASKA_DTO.setAnimalName("Васька");
        VASKA_DTO.setAnimalType("кот");
        VASKA_DTO.setGender("male");
        VASKA_DTO.setDescription("Рыжий кот. Левое ухо ободрано.");
        VASKA_DTO.setBirthDate(LocalDate.of(2023,1,1));
        VASKA_DTO.setNurseryId(1);

        PALKAN_DTO = new AnimalDTO();
        PALKAN_DTO.setAnimalName("Палкан");
        PALKAN_DTO.setAnimalType("пёс");
        PALKAN_DTO.setGender("male");
        PALKAN_DTO.setDescription("Немецкая овчарка.");
        PALKAN_DTO.setBirthDate(LocalDate.of(2020,1,1));
        PALKAN_DTO.setNurseryId(2);

        VASKA = new Animal();
        VASKA.setId(1);
        VASKA.setAnimalName("Васька");
        VASKA.setAnimalType("кот");
        VASKA.setGender("male");
        VASKA.setBirthDate(LocalDate.of(2023,1,1));
        VASKA.setWhoTookPet(1);
        VASKA.setNurseryId(1);
        VASKA.setDescription("Рыжий кот. Левое ухо ободрано.");

        PALKAN = new Animal();
        PALKAN.setId(2);
        PALKAN.setAnimalName("Палкан");
        PALKAN.setAnimalType("пёс");
        PALKAN.setGender("male");
        PALKAN.setBirthDate(LocalDate.of(2023,1,1));
        PALKAN.setWhoTookPet(1);
        PALKAN.setNurseryId(2);
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
