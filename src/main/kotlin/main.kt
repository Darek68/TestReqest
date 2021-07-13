// Sequences

import kotlin.math.min
import kotlin.collections.MutableList as MutableList1

val names: MutableList1<Name> = mutableListOf(
    Name(1,"Вася Пупкин"),
    Name(2,"Иван Сидоров"),
    Name(3,"Сергей Иванов"),
    Name(4,"Марфа Василева"),
    Name(5,"Мария Петровна"))

fun main() {
    val chatService = ChatsService()

    println(chatService.addMes(1,true,"Моя первая реплика товарищю"))
    println(chatService.addMes(1,false,"Чел, да ты нудный какой то.."))
    println(chatService.addMes(2,true,"Моя первая реплика другому товарищю"))
    println("\nБеседа с собеседником ${chatService.getName(1)} :\n  ${chatService.getMes(1,0,100)}")

    println("\n ${chatService.getChats(false)}")

    println("\n ${chatService.getChats(true)}")

    println("\n ${chatService.delMes(1,1)}")
    println("\nБеседа с собеседником ${chatService.getName(1)} :\n  ${chatService.getMes(1,0,100)}")

    println("\n ${chatService.delChat(1)}")
    println("\nБеседа с собеседником ${chatService.getName(1)} :\n  ${chatService.getMes(1,0,100)}")

}

class Name (val id: Int,val name: String){ // собеседники

}
// Предпосылка: при создании моей реплики, она автоматом становится причитанной (как могу не прочитать свое сообщение???)
class Message(
    var id:Int,          // идентификатор сообщения
    val myReply:Boolean, // это моя реплика\собеседника
    val mes:String,      // сообщение
    var read:Boolean,    // прочитано
    var del:Boolean)

class Chat (val id: Int, // идентификатор собеседника
            var messages: MutableList1<Message>
){

    fun add(message: Message): Int {
        message.id = this.messages.size
        this.messages.add(message)
        return this.messages.last().id
    }
    /*
        fun del(id: Int):Boolean{
            val retVal = try {
                this.messages.removeAt(id)
                true
            } catch (e: IndexOutOfBoundsException) {
                false
            }
            return retVal
        } */
    fun get(id: Int,count:Int): String {
        val size = this.messages.size
        if (id >= size) return "" // за пределами диапазона
        val idLast = min((id + count),size) // конец диапазона не больше его размера
        val messages = this.messages.subList(id,idLast)
        // if (messages == null) return ""             такого не может быть
        var retValue = ""
        for (message in messages){//проход по сообщениям
            if (! message.del) retValue += ((if(message.myReply) "Я: " else "собеседник:") + " ${message.mes} \n")
        }
        return retValue
    }
}

class ChatsService (){

    var chats: MutableList1<Chat> = mutableListOf()

    fun delChat(chatId: Int?):String{ //for (note in notes)
        if (chatId == null) return "Не указан id чата"
        return if(chats.removeIf{it.id == chatId}) "Чат с ${getName(chatId)} успешно удален" else "Не найден чат с ${getName(chatId)}"

    }

    fun getName(nameId:Int): String? {
        return names.findLast { it.id == nameId}?.name
    }

    fun findChat(chatId: Int?):Chat?{ //for (note in notes)
        return chats.findLast { it.id == chatId }
    }
    // добавление сообщения
    fun addMes(id:Int?,myReply:Boolean,mes:String): String{ //val l = b?.length ?: -1
        if (id == null) return "Не указан id чата"
        var chat = findChat(id)
        if (chat == null){ chat = Chat(id,ArrayList()) //Chat(id,ArrayList()).also { chat = it }
            chats.add(chat) } // добавим новый чат в список
        // println("Создан чат с id = ${chat.id}  ${chats.count()}")
        val mesId = chat.add(Message(0,myReply,mes,myReply,false)) // автоматом: мои реклики- прочитанные, чужие - нет
        // println("Добавлено сообщение: ${findChat(id)?.id}   ${findChat(id)?.messages?.get(0)?.mes}")
        return "К чату с пользователем ${getName(id)} добавлно сообщение с идентификатором ${mesId}"
    }
    // удаление сообщения
    fun delMes(id:Int?,mesId: Int?): String{
        if (id == null) return "Не указан id чата"
        if (mesId == null) return "Не указан id сообщения"
        val chat = findChat(id)
        if (chat == null) return "Не найден чат с id=$id"
        if (! chat.messages.removeIf { it.id == mesId }) return "Не найдено сообщение с id=$mesId" else {
            if (chat.messages.isEmpty()) chats.removeIf { it.id == id } /* удалим пустой чат */
            return "Сообщение чата с ${getName(id)} с номером $mesId успешно удалено"
        }
    }
    // вывод сообщений чата
    fun getMes(chatId: Int?,mesId: Int?,count: Int?): String{
        if (chatId == null || mesId == null || count == null) return "Указаны неверные параметры"
        val chat = findChat(chatId)
        if (chat == null) return "Чат $chatId не существует"
        return chat.get(mesId,count)
    }
    // список непрочитанных чатов
    fun getUnreadChats(): String{
        val chats: List<Chat> = chats.filter {it.messages.filter {! it.read }.isNotEmpty() }
        if (chats.isEmpty()) return "Нет непрочитанных сообщений"
        return ("Список чатов с непрочитанными сообщениями:\n" + getListChats(chats))
    }
    // список чатов true - всех false - не прочитанных
    fun getChats(read: Boolean): String{
        if (! read) return getUnreadChats()
        val chats: List<Chat> = chats.filter { it.messages.isNotEmpty() }
        return ("Список всех чатов:\n" + getListChats(chats))
    }
    // строка чатов
    fun getListChats(chats: List<Chat>):String{
        var retValue = ""
        for (chat in chats) retValue += "${getName(chat.id)}  \n"
        retValue += "Всего ${chats.count()} чатов"
        return retValue
    }


}
/* *
Должны быть чаты (чат - это общение с одним человеком, т.н. direct messages).
Можно создавать чаты, удалять их, получать список имеющихся чатов.
В каждом чате есть сообщения от 1 до нескольких (см. раздел ниже).
Имеется возможность создавать сообщения, редактировать их и удалять (для простоты - можно удалять и свои, и чужие).
В каждом чате есть прочитанные и непрочитанные сообщения.

Пользователь должен иметь возможность:

Получить информацию о количестве непрочитанных чатов (например, service.getUnreadChatsCount) - это количество чатов, в каждом из которых есть хотя бы одно непрочитанное сообщение.
Получить список чатов (например, service.getChats) - где в каждом чате есть последнее сообщение (если нет, то пишется "нет сообщений").
Получить список сообщений из чата, указав (после того, как вызвана данная функция, все отданные сообщения автоматически считаются прочитанными):
id чата;
id последнего сообщения, начиная с которого нужно подгрузить более новые;
количество сообщений.
Создать новое сообщение.
Удалить сообщение (при удалении последнего сообщения в чате весь чат удаляется).
Создать чат (чат создаётся тогда, когда пользователю, с которым до этого не было чата, отправляется первое сообщение).
Удалить чат (целиком удаляется все переписка).
/
 */