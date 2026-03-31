/** 
  * The "armlet" architecture.
  *
  * @author Petteri Kaski <petteri.kaski@aalto.fi>
  *
  */

package armlet:

  // HELPER SUBROUTINES FOR CONVERSION AND FORMATTING

  def bitsToInt(b: Seq[Boolean]) = 
    b.foldLeft(((0,1))) { (t,v) => (t._1 + (if v then t._2 else 0),t._2*2) }._1
  
  def intToBits(i: Int) = 
    (0 until wordlength).map(j => (i&(1<<j)) != 0)

  def bitsToString(b: Seq[Boolean]) = 
    (b.reverse.map(if _ then "1" else "0").reduceLeft(_ ++ _)) ++ 
    " 0x%04X %5d".format(bitsToInt(b),bitsToInt(b))

  def intToString(i: Int) =
    intToBits(i).reverse.map(if _ then "1" else "0").reduceLeft(_ ++ _)
