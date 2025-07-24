/** @jsxImportSource @emotion/react */
import { useEffect, useRef } from 'react';

import { typography } from '@shared/styles/default.styled';

import * as wheelPicker from './wheelPicker.styled';

interface WheelPickerProps {
  options: string[];
  selectedIndex: number;
  onSelect: (index: number) => void;
}

function WheelPicker({ options, selectedIndex, onSelect }: WheelPickerProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const itemHeight = 48;

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.scrollTop = selectedIndex * itemHeight;
    }
  }, [selectedIndex]);

  const handleScroll = () => {
    if (containerRef.current) {
      const scrollTop = containerRef.current.scrollTop;
      const newIndex = Math.round(scrollTop / itemHeight);
      if (
        newIndex !== selectedIndex &&
        newIndex >= 0 &&
        newIndex < options.length
      ) {
        onSelect(newIndex);
      }
    }
  };

  return (
    <div css={wheelPicker.wheelContainer()}>
      <div
        ref={containerRef}
        css={wheelPicker.wheelScroller()}
        onScroll={handleScroll}
      >
        <div css={wheelPicker.wheelPadding()} />
        {options.map((option, index) => (
          <div
            key={index}
            css={[
              wheelPicker.wheelItem(),
              typography.b1,
              selectedIndex === index && wheelPicker.wheelItemSelected(),
            ]}
            onClick={() => {
              onSelect(index);
              if (containerRef.current) {
                containerRef.current.scrollTop = index * itemHeight;
              }
            }}
          >
            {option}
          </div>
        ))}
        <div css={wheelPicker.wheelPadding()} />
      </div>
    </div>
  );
}

export default WheelPicker;
