package com.unciv.ui.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.unciv.UnCivGame
import com.unciv.models.gamebasics.tr

open class CameraStageBaseScreen : Screen {

    var game: UnCivGame = UnCivGame.Current
    var stage: Stage
    var tutorials = Tutorials()
    var hasPopupOpen = false

    init {
        val resolutions: List<Float> = game.settings.resolution.split("x").map { it.toInt().toFloat() }
        stage = Stage(ExtendViewport(resolutions[0], resolutions[1]), batch)// FitViewport(1000,600)
        Gdx.input.inputProcessor = stage
    }


    override fun show() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {}

    fun displayTutorials(name: String) {
        tutorials.displayTutorials(name,stage)
    }


    companion object {
        var skin = Skin(Gdx.files.internal("skin/flat-earth-ui.json"))

        init{
            resetFonts()
        }

        fun resetFonts(){
            skin.get<TextButton.TextButtonStyle>(TextButton.TextButtonStyle::class.java).font = Fonts().getFont(20)
            skin.get<Label.LabelStyle>(Label.LabelStyle::class.java).apply {
                font = Fonts().getFont(18)
                fontColor= Color.WHITE
            }
            skin.get<TextField.TextFieldStyle>(TextField.TextFieldStyle::class.java).font = Fonts().getFont(18)
            skin.get<SelectBox.SelectBoxStyle>(SelectBox.SelectBoxStyle::class.java).font = Fonts().getFont(20)
            skin.get<SelectBox.SelectBoxStyle>(SelectBox.SelectBoxStyle::class.java).listStyle.font = Fonts().getFont(20)
        }
        internal var batch: Batch = SpriteBatch()
    }

    fun onBackButtonClicked(action:()->Unit){
        stage.addListener(object : InputListener(){
            override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
                if(keycode == Input.Keys.BACK){
                    action()
                    return true
                }
                return false
            }
        })
    }

}


fun Button.disable(){
    touchable= Touchable.disabled
    color= Color.GRAY
}
fun Button.enable() {
    color = Color.WHITE
    touchable = Touchable.enabled
}
fun <E> List<E>.getRandom(): E = if (size == 0) throw Exception() else get((Math.random() * size).toInt())


fun colorFromRGB(r: Int, g: Int, b: Int): Color {
    return Color(r/255f, g/255f, b/255f, 1f)
}

fun Actor.centerX(parent:Actor){ x = parent.width/2 - width/2 }
fun Actor.centerY(parent:Actor){ y = parent.height/2- height/2}
fun Actor.center(parent:Actor){ centerX(parent); centerY(parent)}

fun Actor.centerX(parent:Stage){ x = parent.width/2 - width/2 }
fun Actor.centerY(parent:Stage){ y = parent.height/2- height/2}
fun Actor.center(parent:Stage){ centerX(parent); centerY(parent)}

fun Label.setFontColor(color:Color): Label {style=Label.LabelStyle(style).apply { fontColor=color }; return this}

fun Label.setFontSize(size:Int): Label {
    style = Label.LabelStyle(style)
    style.font = Fonts().getFont(size)
    style = style // because we need it to call the SetStyle function. Yuk, I know.
    return this // for chaining
}



// If there are other buttons that require special clicks then we'll have an onclick that will accept a string parameter, no worries

fun Actor.onClick(sound:String,function: () -> Unit){
    this.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
            if(sound!="") Sounds.play(sound)
            function()
        }
    } )
}

fun Actor.onClick(function: () -> Unit): Actor {
    onClick("click",function)
    return this
}

fun Actor.surroundWithCircle(size:Float): IconCircleGroup {
    return IconCircleGroup(size,this)
}

fun Actor.addBorder(size:Float,color:Color):Table{
    val table = Table()
    table.pad(size)
    table.background = ImageGetter.getBackground(color)
    table.add(this).fill()
    table.pack()
    return table
}

fun Table.addSeparator(): Cell<Image> {
    row()
    val image = ImageGetter.getWhiteDot()
    val cell = add(image).colspan(columns).height(2f).fill()
    row()
    return cell
}

/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> ArrayList<T>.withItem(item:T): ArrayList<T> {
    val newArrayList = ArrayList(this)
    newArrayList.add(item)
    return newArrayList
}

/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> HashSet<T>.withItem(item:T): HashSet<T> {
    val newHashSet = HashSet(this)
    newHashSet.add(item)
    return newHashSet
}

/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> ArrayList<T>.withoutItem(item:T): ArrayList<T> {
    val newArrayList = ArrayList(this)
    newArrayList.remove(item)
    return newArrayList
}


/**
 * Solves concurrent modification problems - everyone who had a reference to the previous arrayList can keep using it because it hasn't changed
 */
fun <T> HashSet<T>.withoutItem(item:T): HashSet<T> {
    val newHashSet = HashSet(this)
    newHashSet.remove(item)
    return newHashSet
}

/** also translates */
fun String.toLabel() = Label(this.tr(),CameraStageBaseScreen.skin)