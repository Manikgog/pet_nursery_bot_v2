package ru.pet.nursery.enumerations;

public enum CallbackDataEnum {
    INFO("info"),
    REPORT("report"),
    VOLUNTEER("volunteer"),
    HEALTH("health"),
    DIET("diet"),
    BEHAVIOUR("behaviour"),
    INSTRUCTION("instruction"),
    FOTO("foto"),
    BACK_TO_REPORT_MENU("back_to_report_menu"),
    ADDRESS_AND_PHONE("address_and_phone"),
    PET_INFORMATION("pet_information"),
    WHAT_NEED_FOR_ADOPTION("what_need_for_adoption"),
    CAT_PHOTO("cat_photo"),
    DOG_PHOTO("dog_photo"),
    CAT_INFORMATION("cat_information"),
    DOG_INFORMATION("dog_information"),
    START("start"),
    CLOSE_CHAT("closeChat");

    private String displayName;

    private CallbackDataEnum(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
