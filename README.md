# Shadow Admin Tool

Mobil adminlar uchun: reportlarni avtohisoblash, onlayn vaqt, shablon javoblar va
"[Ism][ID]: ... [Hisobotlar soni: N]" formatidagi reportlarga avto-javob.

## O'rnatish
- Android Studio: loyihani papka sifatida oching (Gradle avtomatik sinxronlanadi).
- AIDE (telefonda): `app/src/main/java/...` ichidagi `.java` fayllarni va
  `app/src/main/res/...`, `AndroidManifest.xml`ni AIDE loyihasiga import qiling.

Ishga tushirgach:
1. "Accessibility xizmatini yoqish" -> ro'yxatdan "Shadow Admin Tool"ni yoqing.
2. "Overlay ruxsatini berish" -> ruxsat bering.
3. "Overlay panelni ishga tushirish".
4. Panelda: SAMP mobil klientning paket nomini kiriting (masalan
   `com.rockstargames.gtasa` yoki sizning custom build paketingiz), kerak bo'lsa
   report regexni moslang, avto-javob rejimini tanlang, saqlang.

## MUHIM TEXNIK CHEKLOV

AccessibilityService faqat chatni **oddiy Android View** (TextView/EditText) sifatida
chizadigan klientlarda ishlaydi. Agar sizning SAMP mobile build'ingiz chatni
to'g'ridan-to'g'ri o'yin dvijogi (OpenGL/Canvas) orqali ekranga chizsa,
`AccessibilityNodeInfo` bu matnni umuman ko'ra olmaydi - chunki u haqiqiy UI
tugun emas.

Buni tekshirish uchun: Sozlamalar -> Ishlab chiquvchilar uchun -> "Accessibility
ma'lumotlarini tekshirish" vositasi yoki `uiautomatorviewer` (agar kompyuteringiz
bo'lsa) orqali o'yin ochiq turganda ekran tuzilishini ko'ring. Agar chat matni
tugunlar ro'yxatida ko'rinsa - tuls to'liq ishlaydi. Ko'rinmasa, faqat chat
**yozish** maydoni (odatda haqiqiy `EditText`, chunki klaviatura undan foydalanadi)
bilan ishlash mumkin bo'ladi - ya'ni avtomatik yuborish emas, faqat shablonni
tez joylashtirish.

## To'liq avtomatik rejim haqida ogohlantirish

"To'liq avtomatik yubor" rejimi chat inputini topib, matnni o'zi kiritadi va
yuboradi. Bu ishlashi uchun kod ichidagi `findFirstEditable()` va
`ACTION_IME_ENTER` chaqiruvi sizning aniq klientingizga mos kelishi kerak -
ba'zi klientlarda "yuborish" alohida tugma bosishni talab qilishi mumkin
(shu holda kodga qo'shimcha `ACTION_CLICK` qo'shish kerak bo'ladi - aniq
tugun ID'sini bilgach ayta olaman).

Xavfsizlik uchun tavsiya: avval "Faqat tayyorlab qo'y" rejimida sinab ko'ring,
to'g'ri ishlaganidan keyingina to'liq avtomatikka o'ting.
