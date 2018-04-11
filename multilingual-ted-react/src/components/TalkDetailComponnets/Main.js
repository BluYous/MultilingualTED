import React, {Component} from 'react';
import {Avatar, BackTop, Card, Col, Divider, List, Row, Tabs} from 'antd';
import {ajaxAddress} from "../../common";
import PieChart from "./PieChart";
import Subtitle from "./Subtitle";

export default class Main extends Component {
    render() {
        let {talk} = this.props;
        if (talk.talk_id == null) {
            return (
                <div>
                    Loading...
                </div>
            )
        }
        return (
            <div>
                <BackTop/>
                <Row type="flex" justify="center">
                    <Col span={22}>
                        <a target='_blank' href={talk.talk_url}><img alt={talk.title} style={{width: '100%'}}
                                                                     src={`${ajaxAddress}/resource?fileName=${talk.thumb_img_path}`}/></a>
                    </Col>
                </Row>
                <Row type="flex" justify="center">
                    <Col span={22}>
                        <Card title={talk.title} bordered={false} style={{width: '100%'}}>
                            <Tabs defaultActiveKey="detail">
                                <Tabs.TabPane tab="Detail" key="detail">
                                    <div><b>Title: </b>{talk.title}</div>
                                    <div><b>Speaker: </b>{talk.speaker}</div>
                                    <div><b>Description: </b>{talk.description}</div>
                                    <p/>
                                    <div><b>Viewed Count: </b>{talk.viewed_count}</div>
                                    <div><b>Duration: </b>{talk.duration}</div>
                                    <div>
                                        <b>Native Language: </b>
                                        {talk.native_language_name} ({talk.native_language_endonym})
                                    </div>
                                    <div><b>Event: </b>{talk.event_label}</div>
                                    {talk.event_blurb == null ? `` :
                                        <div><b>Event Description: </b>
                                            <span dangerouslySetInnerHTML={{__html: talk.event_blurb}}/>
                                        </div>
                                    }
                                    <p/>
                                    <div><b>Recorded: </b>{talk.recorded_at}</div>
                                    <div><b>Posted: </b>{talk.published_datetime}</div>
                                    <div><b>Last Update: </b>{talk.last_update_datetime}</div>
                                </Tabs.TabPane>
                                <Tabs.TabPane tab="Speaker" key="speaker">
                                    <List
                                        itemLayout="horizontal"
                                        dataSource={talk.speakers}
                                        renderItem={speaker => (
                                            <List.Item>
                                                <List.Item.Meta
                                                    avatar={<Avatar
                                                        src={`${ajaxAddress}/resource?fileName=${speaker.photo_path}`}/>}
                                                    title={<a
                                                        href={`https://www.ted.com/speakers/${speaker.slug}`}>{speaker.first_name} {speaker.last_name}</a>}
                                                    description={
                                                        <div>
                                                            <div><b>Description: </b>{speaker.description}</div>
                                                            {speaker.what_others_say === '' ? `` :
                                                                <div><b>What Others Say: </b>{speaker.what_others_say}
                                                                </div>
                                                            }
                                                            <div><b>Who They Are: </b>{speaker.who_they_are}</div>
                                                            {speaker.why_listen == null ? `` :
                                                                <div><b>Why Listen: </b>
                                                                    <span
                                                                        dangerouslySetInnerHTML={{__html: speaker.why_listen}}/>
                                                                </div>
                                                            }
                                                            {speaker.title === '' ? `` :
                                                                <div><b>Title: </b>{speaker.title}
                                                                </div>
                                                            }
                                                            {speaker.middle_initial === '' ? `` :
                                                                <div><b>Middle Initial: </b>{speaker.middle_initial}
                                                                </div>
                                                            }
                                                        </div>
                                                    }
                                                />
                                            
                                            </List.Item>
                                        )}
                                    />
                                </Tabs.TabPane>
                                <Tabs.TabPane tab="Subtitle" key="subtitle">
                                    <Subtitle talkId={talk.talk_id} curLanguage={talk.language_code}/>
                                </Tabs.TabPane>
                                <Tabs.TabPane tab="More" key="more">
                                    <h3>Ratings</h3>
                                    <div style={{width: "100%", height: "500px"}}>
                                        <PieChart data={
                                            talk.ratings.map((rating, index) => {
                                                return {value: rating.rating_count, name: rating.rating_name}
                                            })
                                        }
                                                  dataName={
                                                      talk.ratings.map((rating, index) => {
                                                          return rating.rating_name
                                                      })
                                                  }
                                        />
                                    </div>
                                    <Divider/>
                                    <h3>Topics ({talk.topics.length})</h3>
                                    <List
                                        grid={{gutter: 16, column: 4}}
                                        dataSource={talk.topics}
                                        renderItem={rating => (
                                            <List.Item>
                                                <span>{rating.label}</span>
                                            </List.Item>
                                        )}
                                    />
                                </Tabs.TabPane>
                            </Tabs>
                        </Card>
                    </Col>
                </Row>
            </div>
        )
    }
}