'use client';

import { useEffect, useState } from 'react';

interface Branch {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
  depth: number;
  delay: number;
  duration: number;
  pathLength: number;
  opacity: number;
  strokeWidth: number;
}

// 가지 생성 로직 개선 (깊이 조절 및 위치 최적화)
function generateBranches(): Branch[] {
  const branches: Branch[] = [];

  function addBranch(
    x1: number,
    y1: number,
    angle: number,
    length: number,
    depth: number,
    delay: number,
  ) {
    // 깊이를 9로 줄여서 성능 최적화 (기존 10)
    if (depth > 8 || length < 4) return;

    const rad = (angle * Math.PI) / 180;
    const x2 = x1 + Math.sin(rad) * length;
    const y2 = y1 - Math.cos(rad) * length;
    const dur = 0.4 + (9 - depth) * 0.05;

    branches.push({
      x1,
      y1,
      x2,
      y2,
      depth,
      delay,
      duration: dur,
      pathLength: Math.hypot(x2 - x1, y2 - y1),
      opacity: Math.max(0.2, 1 - depth * 0.08),
      strokeWidth: Math.max(0.5, 4.0 - depth * 0.35),
    });

    const nextDelay = delay + dur * 0.4; // 딜레이 약간 단축
    // spread: 가지가 벌어지는 각도 (너비 조절)
    const spread = depth < 2 ? 24 : depth < 5 ? 28 : 32;
    const shrink = 0.7;

    addBranch(x2, y2, angle - spread, length * shrink, depth + 1, nextDelay);
    addBranch(x2, y2, angle + spread, length * shrink, depth + 1, nextDelay);

    // 중앙 가지 확률 조정
    if (depth >= 1 && depth < 5) {
      addBranch(x2, y2, angle, length * 0.55, depth + 2, nextDelay + 0.1);
    }
  }

  // 시작점 Y를 800으로 변경하여 바닥에 딱 붙게 수정 (기존 790), 길이 140 -> 180으로 증가
  addBranch(350, 800, 0, 170, 0, 0);

  return branches;
}

const BRANCHES = generateBranches();

export function TreeAnimation() {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) return null;

  return (
    <div className="absolute inset-0 w-full h-full pointer-events-none overflow-hidden">
      <svg
        viewBox="0 0 700 800"
        preserveAspectRatio="xMidYMax meet"
        className="w-full h-full"
      >
        <defs>
          <radialGradient id="treeGlow" cx="50%" cy="100%" r="60%">
            <stop offset="0%" stopColor="rgba(139,92,246,0.15)" />
            <stop offset="100%" stopColor="rgba(0,0,0,0)" />
          </radialGradient>
          <style>
            {`
              @keyframes drawLine {
                to { stroke-dashoffset: 0; }
              }
              .tree-branch {
                will-change: stroke-dashoffset;
              }
            `}
          </style>
        </defs>

        {/* 바닥 글로우 효과 위치 조정 */}
        <ellipse cx="350" cy="800" rx="300" ry="40" fill="url(#treeGlow)" />

        {BRANCHES.map((branch, idx) => (
          <line
            key={idx}
            x1={branch.x1}
            y1={branch.y1}
            x2={branch.x2}
            y2={branch.y2}
            stroke={branch.depth > 6 ? '#86efac' : 'white'} // 깊이 8,9인 끝 가지는 은은한 연두색 (green-300)
            strokeWidth={branch.strokeWidth}
            strokeLinecap="round"
            className="tree-branch"
            style={{
              opacity: branch.opacity,
              strokeDasharray: branch.pathLength,
              strokeDashoffset: branch.pathLength,
              animation: `drawLine ${branch.duration}s ease-out forwards ${branch.delay}s`,
            }}
          />
        ))}
      </svg>
    </div>
  );
}
