# 尸道纪元 · 僵尸感染扩展

 
上古尸道法则复苏，天地间尸气蔓延，原本懵懂的凡俗僵尸尽数开启灵智，习得种毒化尸、吞灵炼体、尸骸渡功之能，更可借天雷淬体渡劫，步步蜕变。本模组以修仙体系重构僵尸本源，赋予其完整的猎食、传承、成长、避劫之道，寻常僵尸亦可凭杀戮步步成长，最终蜕变为尸王尸皇，成为生存路上真正的灭世级威胁。
  
 
#
## 尸傀猎食 · 万灵为饵
 
原版僵尸仅追猎域外天魔与平常百姓。
尸道复苏之后，僵尸凶性彻底觉醒，凡天地间有血肉生气者，尽皆为其猎食目标。唯同类僵尸、与阴煞同源的苦力怕不在其攻击之列，上至铁傀守灵、骷髅阴兵，下至猪牛羊畜、狼蛛野怪，皆会被僵尸主动追索袭杀。
 
#
## 尸毒种身 · 碧烟为记
 
僵尸利爪伤及生灵时，便会将一缕尸毒打入对方体内，种下「尸毒缠身」（僵尸感染）之厄，效果持续一个时辰（60秒）。
中毒者足下萦绕碧色尸烟，肉眼可辨；此毒不直接损耗精血生机，只为埋下尸种，待身死之时引动尸化之变。
 
#
身死化尸 · 衣钵相承
 
身中尸毒者若在毒效存续期间身亡，尸身会立刻被尸气彻底侵染，原地凝出一具新生僵尸，冠以原主名号后缀“尸傀（僵尸）”之称。
原主随身穿戴的所有甲胄、兵刃法器，皆会被新生僵尸炼化承袭，如常佩戴使用。
 
新生僵尸将承袭施毒母体的尸道修为，自身根基为凡尸的五成，再叠加母体七成的额外道行：
 
- 基础气血：凡尸半数（10点） + 母体累积额外气血 × 70%
- 基础凶力：凡尸半数（1.5点） + 母体累积额外凶力 × 70%
 
#
尸骸渡功 · 近者承继
 
僵尸身陨之时，周身尸气不会即刻散逸，会以自身为中心，搜寻二十丈内存活的同类僵尸，将自身毕生三成修为渡给距离最近的继承者。
继承者将获得亡者30%的气血上限与凶力加成；若方圆二十丈内无同类，尸气便自行消散，不生传承。
 
#
煞气相震 · 斩尸反噬
 
但凡修士生灵斩杀僵尸，便会被其散逸的尸煞本源反震，承受自身攻击力两成的反噬伤害。
此伤源自尸煞根本，显化为荆棘之相，无可闪避，亦无法豁免。
 
#
尸性通灵 · 趋吉避凶
 
僵尸久沐尸气，对爆裂煞息极为敏感。周遭苦力怕酝酿自爆、或是TNT被引动燃线之时，附近僵尸便会感知到灾劫气息，主动退避至爆炸波及范围之外，保全自身尸骸。
 
#
天雷淬体 · 尸道渡劫
 
僵尸若遭天雷劈中，便有机会触发尸道天劫，借雷霆之力炼骨淬体。
 
- 渡劫成功率为一成（10%），渡劫功成者，气血、凶力、行速皆在自身根基上翻倍，更凝出尸煞护甲（+3.9点），凶威暴涨。
- 无论渡劫成败，雷霆之力都会先灼伤尸骸本源：即刻扣除自身最大气血三成的天雷伤害，并受雷毒侵体，筋骨酸软二十息（20秒虚弱II）。
 
#
吞灵炼体 · 以杀证道
 
僵尸每斩杀一具生灵，便会吞噬其生气精血，用以淬炼自身尸骸，获得永久道行精进：
 
- 最大气血永久提升1点，且炼化生气的瞬间补满周身气血
- 近身凶力永久提升1点
 
此般加成永世留存，不随岁月消散。一具存活日久、杀戮无数的僵尸，终将成长为血厚力沉的尸中霸主，等闲修士难撄其锋。
僵尸自身累积的所有道行，皆会以七成比例，通过尸毒传承给其所化的新生僵尸，代代相承，凶威不减。
 
#
天道制衡 · 三代而竭
 
天道自有制衡，尸道传承有天然衰劫，感染尸脉最多延续三代。初代僵尸种毒化出二代，二代可再化三代，三代所种之毒再无尸化之能，以此避免尸潮无边、倾覆天地，防止天地灵气失衡。

Installation information
=======

This template repository can be directly cloned to get you started with a new
mod. Simply create a new repository cloned from this one, by following the
instructions provided by [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The usual recommendation for an IDE is either IntelliJ IDEA or Eclipse.

If at any point you are missing libraries in your IDE, or you've run into problems you can
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
============
The MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

MDG Legacy:
==========
This template uses [ModDevGradle Legacy](https://github.com/neoforged/ModDevGradle). Documentation can be found [here](https://github.com/neoforged/ModDevGradle/blob/main/LEGACY.md).

Additional Resources: 
==========
Community Documentation: https://docs.neoforged.net/  
NeoForged Discord: https://discord.neoforged.net/
