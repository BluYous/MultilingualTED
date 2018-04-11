import React from 'react';
import {Col, Row, Timeline, Tooltip} from 'antd';

export default class SubtitleDetail extends React.Component {
    onMouseOver = (e) => {
        e.target.style.background = '#e5e5e5';
    };
    
    onMouseOut = (e) => {
        e.target.style.background = '';
    };
    
    parseParagraph = (paragraph) => {
        return (
            <Timeline>
                {
                    paragraph.map((cuesObj, index) => {
                        return (
                            <Timeline.Item key={index} dot={<span>{cuesObj.minute}</span>}>
                                {
                                    cuesObj.cues.map((cue, index) => {
                                        return (
                                            <Tooltip key={index} title={cue.time}>
                                                                <span onMouseOver={this.onMouseOver}
                                                                      onMouseOut={this.onMouseOut}>{cue.text}</span>
                                                <span> </span>
                                            </Tooltip>
                                        )
                                    })
                                }
                            </Timeline.Item>
                        )
                    })
                }
            </Timeline>
        )
    };
    
    render() {
        let {subtitles} = this.props;
        if (subtitles.length === 0) {
            return <span>You can read the subtitles by selecting one or two languages from the selector.</span>
        } else if (subtitles.length === 1) {
            return this.parseParagraph(subtitles[0]);
        } else {
            return (
                <Row type="flex" justify="center">
                    <Col span={11}>{this.parseParagraph(subtitles[0])}</Col>
                    <Col span={11} offset={1}>{this.parseParagraph(subtitles[1])}</Col>
                </Row>
            )
        }
    }
}