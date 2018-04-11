import React, {Component} from 'react';
import {Card, List} from 'antd';
import {ajaxAddress} from "../common";
import {Link} from "react-router";

class TalkFilterListPerPage extends Component {
    render() {
        let {subTalkList, languageFilter} = this.props;
        return (
            <div>
                <List
                    grid={{gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4}}
                    dataSource={subTalkList}
                    renderItem={item => (
                        <List.Item>
                            <Card.Grid key={item.talk_id} style={{width: '100%', height: '400px'}}>
                                <Card
                                    bordered={false}
                                    cover={
                                        <Link
                                            target={'_blank'}
                                            to={`/talk/${item.talk_id}/${languageFilter == null ? `` : `${languageFilter}`}`}>
                                            <img alt={item.title} style={{width: '100%'}}
                                                 src={`${ajaxAddress}/resource?fileName=${item.thumb_img_path}&quality=low`}/>
                                        </Link>
                                    }
                                >
                                    <div style={{
                                        color: '#666',
                                        fontSize: '14px',
                                        fontWeight: '400'
                                    }}>{item.speaker}</div>
                                    <Link
                                        target={'_blank'}
                                        to={`/talk/${item.talk_id}/${languageFilter == null ? `` : `${languageFilter}`}`}
                                        style={{
                                            color: '#111',
                                            fontSize: '15px',
                                            fontWeight: '900'
                                        }}>
                                        {item.title}
                                    </Link>
                                    <div style={{color: '#666', fontSize: '12px', fontWeight: '400'}}>
                                        <b>Posted </b>{item.published_datetime}</div>
                                    {
                                        item.rated == null ? '' :
                                            <div style={{
                                                color: '#666',
                                                fontSize: '12px',
                                                fontWeight: '400'
                                            }}>
                                                <b>Rated </b>{item.rated}</div>
                                    }
                                    <div style={{color: '#666', fontSize: '12px', fontWeight: '400'}}>
                                        <b>Duration </b>{item.duration}</div>
                                </Card>
                            </Card.Grid>
                        </List.Item>
                    )}
                />
            </div>
        )
    }
}

export default TalkFilterListPerPage;