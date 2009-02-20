/**
 Insert copyright boilerplate here
*/

package com.pocketchangeapp.model

import java.math.MathContext

import net.liftweb.mapper._

class Account extends LongKeyedMapper[Account] with IdPK {
  def getSingleton = Account

  object owner extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  def admins = AccountAdmin.findAll(By(AccountAdmin.account, this.id))

  def addAdmin (user : User) = AccountAdmin.create.account(this).admin(user).save

  def viewers = AccountViewer.findAll(By(AccountViewer.account, this.id))

  object is_public extends MappedBoolean(this) {
    override def defaultValue = false
  }

  // The balance has up to 16 digits and 2 decimal places
  object balance extends MappedDecimal(this, MathContext.DECIMAL64, 2)

  def transactions = Transaction.findAll(By(Transaction.account, this.id))

  def tags = Tag.findAll(By(Tag.account, this.id))

  object name extends MappedString(this,100)

  object description extends MappedString(this, 300)

  object externalAccount extends MappedString(this, 300)

  def notes = AccountNote.findAll(By(AccountNote.account, this.id))
}

object Account extends Account with LongKeyedMetaMapper[Account] {
  override def dbTableName = "accounts"
  
  def findByName (owner : User, name : String) : List[Account] = 
    Account.findAll(By(Account.owner, owner.id.is), By(Account.name, name))
}

// Rights classes
class AccountAdmin extends LongKeyedMapper[AccountAdmin] with IdPK {
  def getSingleton = AccountAdmin

  object account extends MappedLongForeignKey(this, Account) {
    override def dbIndexed_? = true
  }

  object admin extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }
}

object AccountAdmin extends AccountAdmin with LongKeyedMetaMapper[AccountAdmin] {
  override def dbTableName = "account_admins"
}

class AccountViewer extends LongKeyedMapper[AccountViewer] with IdPK {
  def getSingleton = AccountViewer

  object account extends MappedLongForeignKey(this, Account) {
    override def dbIndexed_? = true
  }

  object viewer extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }
}

object AccountViewer extends AccountViewer with LongKeyedMetaMapper[AccountViewer] {
  override def dbTableName = "account_viewers"
}

// Extra
class AccountNote extends LongKeyedMapper[AccountNote] with IdPK {
  def getSingleton = AccountNote

  object account extends MappedLongForeignKey(this, Account) {
    override def dbIndexed_? = true
  }

  object note extends MappedText(this)
}

object AccountNote extends AccountNote with LongKeyedMetaMapper[AccountNote] {
  override def dbTableName = "account_notes"

  override def fieldOrder = note :: Nil
}