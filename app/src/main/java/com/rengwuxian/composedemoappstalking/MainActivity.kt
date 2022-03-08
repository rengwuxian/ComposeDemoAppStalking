package com.rengwuxian.composedemoappstalking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rengwuxian.composedemoappstalking.ui.theme.BackgroundWhite
import com.rengwuxian.composedemoappstalking.ui.theme.Gray
import com.rengwuxian.composedemoappstalking.ui.theme.Orange
import com.rengwuxian.composedemoappstalking.ui.theme.LightPink

class MainActivity : ComponentActivity() {
  var currentLove: Love? by mutableStateOf(null)
  var currentLovePageState by mutableStateOf(LovePageState.Closed)
  var cardSize by mutableStateOf(IntSize(0, 0))
  var fullSize by mutableStateOf(IntSize(0, 0))
  var cardOffset by mutableStateOf(IntOffset(0, 0))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Column(Modifier.onSizeChanged { fullSize = it }) {
        Column(Modifier.fillMaxWidth().weight(1f).background(BackgroundWhite)
            .verticalScroll(rememberScrollState())) {
          TopBar()
          SearchBar()
          NamesBar()
          LovesArea(
            { cardSize = it },
            { love, offset ->
              currentLove = love
              currentLovePageState = LovePageState.Opening
              cardOffset = offset
            })
          PlaceArea()
        }
        NavBar()
      }
      LoveDetailsPage(currentLove, currentLovePageState, cardSize, fullSize, cardOffset, {
        currentLovePageState = LovePageState.Closing
      }, {
        currentLovePageState = LovePageState.Closed
      })
    }
  }
}

@Composable
fun TopBar() {
  Row(Modifier.fillMaxWidth().padding(28.dp, 28.dp, 28.dp, 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Image(painterResource(R.drawable.avatar_rengwuxian), "头像",
      Modifier.clip(CircleShape).size(64.dp))
    Column(Modifier.padding(start = 14.dp).weight(1f)) {
      Text("欢迎回来！", fontSize = 14.sp, color = Gray)
      Text("小朱", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
    Surface(Modifier.clip(CircleShape), color = LightPink) {
      Image(painterResource(R.drawable.ic_notification_new), "通知",
        Modifier.padding(10.dp).size(32.dp))
    }
  }
}

@Composable
fun SearchBar() {
  Row(Modifier.padding(24.dp, 2.dp, 24.dp, 6.dp).fillMaxWidth().height(56.dp)
      .clip(RoundedCornerShape(28.dp)).background(Color.White),
    verticalAlignment = Alignment.CenterVertically
  ) {
    var searchText by remember { mutableStateOf("") }
    BasicTextField(searchText, { searchText = it },
      Modifier.padding(start = 24.dp).weight(1f),
      textStyle = TextStyle(fontSize = 15.sp)
    ) {
      if (searchText.isEmpty()) {
        Text("搜搜看？", color = Color(0xffb4b4b4), fontSize = 15.sp)
      }
      it()
    }
    Box(Modifier.padding(6.dp).fillMaxHeight().aspectRatio(1f)
      .clip(CircleShape).background(Color(0xfffa9e51))
    ) {
      Icon(painterResource(R.drawable.ic_search), "搜索",
        Modifier.size(24.dp).align(Alignment.Center), tint = Color.White
      )
    }
  }
}

@Composable
fun NamesBar() {
  val names = listOf("扔物线", "朱凯", "老冯", "郝哥", "张三", "狐狸精", "孙悟空", "唐僧")
  var selected by remember { mutableStateOf(0) }
  LazyRow(Modifier.padding(0.dp, 8.dp), contentPadding = PaddingValues(12.dp, 0.dp)) {
    itemsIndexed(names) { index, name ->
      Column(Modifier.padding(12.dp, 4.dp).width(IntrinsicSize.Max)) {
        Text(name, fontSize = 15.sp,
          color = if (index == selected) Color(0xfffa9e51) else Color(0xffb4b4b4)
        )
        Box(Modifier.fillMaxWidth().padding(top = 4.dp).height(2.dp).clip(RoundedCornerShape(1.dp))
            .background(if (index == selected) Color(0xfffa9e51) else Color.Transparent
            )
        )
      }
    }
  }
}

@Composable
fun LovesArea(onCardSizedChanged: (IntSize) -> Unit,
              onCardClicked: (love: Love, offset: IntOffset) -> Unit) {
  Column {
    Row(Modifier.padding(24.dp, 12.dp).fillMaxWidth()
    ) {
      Text("TA 爱的", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      Spacer(Modifier.weight(1f))
      Text("查看全部", fontSize = 15.sp, color = Color(0xffb4b4b4))
    }
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(24.dp),
      contentPadding = PaddingValues(24.dp, 0.dp)
    ) {
      itemsIndexed(loves) { index, love ->
        var intOffset: IntOffset? by remember { mutableStateOf(null) }
        Button(onClick = { onCardClicked(love, intOffset!!) },
          Modifier.width(220.dp)
            .onSizeChanged { if (index == 0) onCardSizedChanged(it) }
            .onGloballyPositioned {
              val offset = it.localToRoot(Offset(0f, 0f))
              intOffset = IntOffset(offset.x.toInt(), offset.y.toInt())
            },
          shape = RoundedCornerShape(16.dp),
          contentPadding = PaddingValues(6.dp),
          colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
          Column {
            Image(painterResource(love.imageId), "图像",
              Modifier.clip(RoundedCornerShape(16.dp)).fillMaxWidth().aspectRatio(1.35f),
              contentScale = ContentScale.Crop,
              alignment = Alignment.Center
            )
            Row(
              Modifier.padding(8.dp, 12.dp, 8.dp, 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column() {
                Text(love.name, color = Color.Black, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Text(love.category, color = Color(0xffb4b4b4), fontSize = 14.sp)
              }
              Spacer(Modifier.weight(1f))
              Row(
                Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xfffef1e6))
                  .padding(6.dp, 11.dp, 8.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(painterResource(R.drawable.ic_star), "", Modifier.size(24.dp),
                  tint = Color(0xfffa9e51)
                )
                Text(love.score.toString(), color = Color(0xfffa9e51), fontSize = 14.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun PlaceArea() {
  val place = Place("扔物线学堂办公室", "郑州", "5 分钟前", R.drawable.img_xuetang)
  Column(Modifier.padding(24.dp, 24.dp, 24.dp, 0.dp)) {
    Text(
      "TA 去过", fontSize = 16.sp, fontWeight = FontWeight.Bold
    )
    Surface(
      Modifier
        .fillMaxWidth()
        .padding(top = 12.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .padding(8.dp),
    ) {
      Row(Modifier.height(IntrinsicSize.Max)) {
        Image(
          painterResource(place.imageId),
          "图像",
          Modifier
            .clip(RoundedCornerShape(16.dp))
            .size(80.dp),
          contentScale = ContentScale.Crop,
          alignment = Alignment.Center
        )
        Column(
          Modifier
            .padding(12.dp, 0.dp)
            .fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly
        ) {
          Text(place.time, fontSize = 14.sp, color = Color(0xffb4b4b4))
          Text(place.name, fontSize = 16.sp)
          Text(place.city, fontSize = 14.sp, color = Color(0xffb4b4b4))
        }
      }
    }
  }
}

@Composable
fun NavBar() {
  Row(
    Modifier
      .background(Color.White)
      .height(84.dp)
      .padding(16.dp, 0.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    NavItem(R.drawable.ic_home, "首页", Orange)
    NavItem(R.drawable.ic_tag, "标签", Gray)
    NavItem(R.drawable.ic_calendar, "日历", Gray)
    NavItem(R.drawable.ic_me, "我", Gray)
  }
}

@Composable
fun RowScope.NavItem(@DrawableRes iconRes: Int, description: String, tint: Color) {
  Button(onClick = {  },
    Modifier
      .weight(1f)
      .fillMaxHeight(),
    shape =  RectangleShape,
    colors =  ButtonDefaults.outlinedButtonColors()
  ) {
    Icon(painterResource(iconRes), description,
      Modifier
        .size(24.dp)
        .weight(1f),
      tint = tint)
  }
}

@Composable
fun LoveDetailsPage(
  love: Love?,
  pageState: LovePageState,
  cardSize: IntSize,
  fullSize: IntSize,
  cardOffset: IntOffset,
  onPageClosing: () -> Unit,
  onPageClosed: () -> Unit
) {
  var animReady by remember { mutableStateOf(false) }
  val background by animateColorAsState(
    if (pageState > LovePageState.Closed) Color(0xfff8f8f8) else Color.White,
    finishedListener = {
      if (pageState == LovePageState.Closing) {
        onPageClosed()
        animReady = false
      }
    })
  val cornerSize by animateDpAsState(if (pageState > LovePageState.Closed) 0.dp else 16.dp)
  val paddingSize by animateDpAsState(if (pageState > LovePageState.Closed) 10.dp else 6.dp)
  val size by animateIntSizeAsState(if (pageState > LovePageState.Closed) fullSize else cardSize)
  val titleOuterPaddingHorizontal by animateDpAsState(if (pageState > LovePageState.Closed) 14.dp else 0.dp)
  val titlePaddingHorizontal by animateDpAsState(if (pageState > LovePageState.Closed) 16.dp else 8.dp)
  val titlePaddingTop by animateDpAsState(if (pageState > LovePageState.Closed) 18.dp else 12.dp)
  val titlePaddingBottom by animateDpAsState(if (pageState > LovePageState.Closed) 16.dp else 8.dp)
  val titleOffsetY by animateDpAsState(if (pageState > LovePageState.Closed) (-40).dp else 0.dp)
  val titleFontSize by animateFloatAsState(if (pageState > LovePageState.Closed) 18f else 15f)
  val titleFontWeight by animateIntAsState(if (pageState > LovePageState.Closed) 900 else 700)
  val titleSpacing by animateDpAsState(if (pageState > LovePageState.Closed) 10.dp else 4.dp)
  val subtitleFontSize by animateFloatAsState(if (pageState > LovePageState.Closed) 15f else 14f)
  val badgeCornerSize by animateDpAsState(if (pageState > LovePageState.Closed) 15.dp else 10.dp)
  val badgeWidth by animateDpAsState(if (pageState > LovePageState.Closed) 90.dp else 0.dp)
  val badgeHeight by animateDpAsState(if (pageState > LovePageState.Closed) 66.dp else 0.dp)
  val badgeBackground by animateColorAsState(
    if (pageState > LovePageState.Closed) Color(0xfffa9e51) else Color(
      0xfffef1e6
    )
  )
  val badgeContentColor by animateColorAsState(
    if (pageState > LovePageState.Closed) Color.White else Color(
      0xfffa9e51
    )
  )
  val imageCornerSize by animateDpAsState(if (pageState > LovePageState.Closed) 32.dp else 16.dp)
  val imageRatio by animateFloatAsState(if (pageState > LovePageState.Closed) 1f else 1.35f)
  val fullOffset = remember { IntOffset(0, 0) }
  val offsetAnimatable = remember { Animatable(IntOffset(0, 0), IntOffset.VectorConverter) }
  LaunchedEffect(pageState) {
    when (pageState) {
      LovePageState.Opening -> {
        animReady = true
        offsetAnimatable.snapTo(cardOffset)
        offsetAnimatable.animateTo(fullOffset)
      }
      LovePageState.Closing -> {
        offsetAnimatable.snapTo(fullOffset)
        offsetAnimatable.animateTo(cardOffset)
      }
      else -> {}
    }
  }
  if (pageState != LovePageState.Closed && animReady) {
    Box(
      Modifier
        .offset { offsetAnimatable.value }
        .clip(RoundedCornerShape(cornerSize))
        .width(with(LocalDensity.current) { size.width.toDp() })
        .height(with(LocalDensity.current) { size.height.toDp() })
        .background(background)
        .padding(paddingSize)
    ) {
      Column(Modifier.verticalScroll(rememberScrollState())) {
        Image(
          painterResource(love!!.imageId),
          "图像",
          Modifier
            .clip(RoundedCornerShape(imageCornerSize))
            .fillMaxWidth()
            .aspectRatio(imageRatio),
          contentScale = ContentScale.Crop,
          alignment = Alignment.Center
        )
        Row(
          Modifier
            .offset(0.dp, titleOffsetY)
            .padding(titleOuterPaddingHorizontal, 0.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(
              titlePaddingHorizontal,
              titlePaddingTop,
              titlePaddingHorizontal,
              titlePaddingBottom
            ),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column() {
            Text(
              love.name,
              color = Color.Black,
              fontSize = titleFontSize.sp,
              fontWeight = FontWeight(titleFontWeight)
            )
            Spacer(Modifier.height(titleSpacing))
            Text(love.category, color = Color(0xffb4b4b4), fontSize = subtitleFontSize.sp)
          }
          Spacer(Modifier.weight(1f))
          Box(
            Modifier
              .width(badgeWidth)
              .height(badgeHeight)
              .clip(RoundedCornerShape(badgeCornerSize))
              .background(badgeBackground)
              .padding(6.dp, 11.dp, 8.dp, 8.dp)
          ) {
            Text(
              love.scoreText,
              Modifier.align(Alignment.TopCenter),
              color = badgeContentColor,
              fontSize = 14.sp
            )
            Row(
              Modifier.align(Alignment.BottomCenter),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                painterResource(R.drawable.ic_star),
                "",
                Modifier.size(24.dp),
                tint = badgeContentColor
              )
              Text(love.score.toString(), color = badgeContentColor, fontSize = 14.sp)
            }
          }
        }
        Text(
          "TA 的评价",
          Modifier
            .offset(0.dp, titleOffsetY)
            .padding(14.dp, 24.dp, 14.dp, 14.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
        Text(
          love.description,
          Modifier
            .offset(0.dp, titleOffsetY)
            .padding(14.dp, 0.dp), fontSize = 15.sp, color = Color(0xffb4b4b4)
        )
      }
      Surface(
        { onPageClosing() },
        Modifier.padding(14.dp, 32.dp),
        color = Color.White,
        shape = CircleShape,
        indication = rememberRipple()
      ) {
        Icon(
          painterResource(R.drawable.ic_back),
          "返回",
          Modifier
            .padding(8.dp)
            .size(26.dp), tint = Color.Black
        )
      }
      Text(
        "详情",
        Modifier
          .align(Alignment.TopCenter)
          .padding(44.dp),
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Surface(
        { },
        Modifier
          .align(Alignment.TopEnd)
          .padding(14.dp, 32.dp),
        color = Color.White,
        shape = CircleShape,
        indication = rememberRipple()
      ) {
        Icon(
          painterResource(R.drawable.ic_more),
          "更多",
          Modifier
            .padding(8.dp)
            .size(26.dp), tint = Color.Black
        )
      }
    }
  }
}

data class Love(
  val name: String,
  val category: String,
  val score: Float,
  val scoreText: String,
  @DrawableRes val imageId: Int,
  val description: String
)

data class Place(
  val name: String,
  val city: String,
  val time: String,
  @DrawableRes val imageId: Int
)

val loves = mutableStateListOf(
  Love("静电容键盘", "生产工具", 4.4f, "比较喜欢", R.drawable.img_keyboard, "好东西"),
  Love("烤串", "心情修复用品", 4.8f, "非常喜欢", R.drawable.img_kaochuan, "好东西"),
  Love(
    "老婆",
    "赚钱为了啥？",
    5f,
    "完美无瑕",
    R.drawable.img_laopo,
    """
      忘了是怎么开始 也许就是对你 有一种感觉
      忽然间发现自己 已深深爱上你 真的很简单

      爱的地暗天黑都已无所谓 是是非非无法抉择
      没有后悔为爱日夜去跟随 那个疯狂的人是我

      I love you 无法不爱你 baby 说你也爱我
      I love you 永远不愿意 baby 失去你

      不可能更快乐 只要能在一起 做什么都可以
      虽然 世界变个不停 用最真诚的心 让爱变得简单
      
      爱的地暗天黑都已无所谓 是是非非无法抉择
      没有后悔为爱日夜去跟随 那个疯狂的人是我

      I love you 一直在这里 baby 一直在爱你
      I love you 永远都不放弃 这爱你的权利
      如果你还有一些困惑 贴着我的心倾听
      听我说着爱你 Yes I do

      I love you 永远都不放弃 这爱你的权力
    """.trimIndent()
  )
)

enum class LovePageState {
  Closing, Closed, Opening, Open
}