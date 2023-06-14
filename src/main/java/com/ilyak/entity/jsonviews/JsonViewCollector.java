package com.ilyak.entity.jsonviews;

public interface JsonViewCollector {

    interface BaseEntity {
        interface Default {

        }
    }


    interface PushMessage extends BaseEntity{
        interface BasicView extends Default {

        }
    }

    interface Contract extends BaseEntity{
        interface BasicView extends Default {

        }
    }

    interface RentOffer extends BaseEntity{
        interface BasicView extends Default{

        }
        interface WithDates extends BasicView{

        }
        interface OnlyDates{}
    }
    interface Chat extends BaseEntity{
        interface BasicView extends Default {

        }

    }

    interface Rating extends BaseEntity{
        interface BasicView extends Default{

        }
    }

    interface Message{
        interface BasicView extends BaseEntity.Default {

        }

    }
    interface User{

        interface BasicView extends BaseEntity.Default {
        }
        interface WithPassword extends BasicView {
        }

        interface ForPost extends WithAvatarsList{

        }
        interface WithAvatarsList extends BasicView{
        }
    }


    interface Flat{
        interface BasicView extends BaseEntity.Default {
        }
    }

    interface Post{
        interface BasicView extends BaseEntity.Default {

        }

        interface FullyUser extends User.BasicView{

        }

        interface WithModerator extends BasicView{
        }


    }
}
